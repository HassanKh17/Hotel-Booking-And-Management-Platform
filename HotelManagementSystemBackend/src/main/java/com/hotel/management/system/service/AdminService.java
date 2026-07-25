package com.hotel.management.system.service;

import com.hotel.management.system.dto.HotelOwnerDto;
import com.hotel.management.system.exception.DuplicateResourceException;
import com.hotel.management.system.exception.ResourceNotFoundException;
import com.hotel.management.system.mapper.HotelOwnerMapper;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.repository.HotelOwnerRepository;
import com.hotel.management.system.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import com.hotel.management.system.repository.AdminRepository;
import com.hotel.management.system.repository.BookingItemRepository;
import com.hotel.management.system.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AdminService {

    private static final BigDecimal OPENING_BALANCE = new BigDecimal("5000.00");

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final HotelOwnerRepository hotelOwnerRepository;
    private final HotelOwnerMapper hotelOwnerMapper;
    
    
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final BookingItemRepository bookingItemRepository;

    public AdminService(UserRepository userRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            HotelOwnerRepository hotelOwnerRepository,
            HotelOwnerMapper hotelOwnerMapper,
            TransactionService transactionService,
            BookingItemRepository bookingItemRepository,
            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.hotelOwnerRepository = hotelOwnerRepository;
        this.hotelOwnerMapper = hotelOwnerMapper;
        this.transactionService= transactionService;
        this.bookingItemRepository = bookingItemRepository;
        this.transactionRepository= transactionRepository;
    }

    public HotelOwnerDto addOwner(HotelOwnerDto request) {
        if (hotelOwnerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (hotelOwnerRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        HotelOwner owner = hotelOwnerMapper.toEntity(request);
        owner.setBalance(OPENING_BALANCE);

        HotelOwner savedOwner = hotelOwnerRepository.save(owner);
        
        transactionService.recordOpeningBalance(savedOwner, OPENING_BALANCE);
        return hotelOwnerMapper.apply(savedOwner);
    }

    public HotelOwnerDto editOwner(Long ownerId, HotelOwnerDto request) {
        HotelOwner owner = hotelOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel owner not found with id: " + ownerId));

        if (request.email() != null
                && !request.email().isBlank()
                && !request.email().equals(owner.getEmail())
                && hotelOwnerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (request.username() != null
                && !request.username().isBlank()
                && !request.username().equals(owner.getUsername())
                && hotelOwnerRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        hotelOwnerMapper.updateEntity(owner, request);

        HotelOwner updatedOwner = hotelOwnerRepository.save(owner);
        return hotelOwnerMapper.apply(updatedOwner);
    }

    @Transactional
    public void removeOwner(Long ownerId) {
        if (!hotelOwnerRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Hotel owner not found with id: " + ownerId);
        }

        bookingItemRepository.deleteByOwnerId(ownerId);
        transactionRepository.deleteByOwnerHotelIds(ownerId);
        transactionRepository.deleteByOwnerId(ownerId);
        hotelOwnerRepository.deleteById(ownerId);
    }

    public List<HotelOwnerDto> viewOwnerOverview() {
        return hotelOwnerRepository.findAll()
                .stream()
                .map(hotelOwnerMapper)
                .toList();
    }

    public void updateGlobalCharges(BigDecimal percentageIncrease) {
        List<HotelOwner> owners = hotelOwnerRepository.findAll();

        for (HotelOwner owner : owners) {
            BigDecimal current = owner.getBalance() != null ? owner.getBalance() : BigDecimal.ZERO;

            BigDecimal increaseAmount = current
                    .multiply(percentageIncrease)
                    .divide(new BigDecimal("100"));

            owner.setBalance(current.add(increaseAmount));
        }

        hotelOwnerRepository.saveAll(owners);
    }
}
