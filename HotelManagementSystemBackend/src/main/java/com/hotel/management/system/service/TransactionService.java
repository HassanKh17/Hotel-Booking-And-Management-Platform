package com.hotel.management.system.service;

import com.hotel.management.system.dto.OwnerStatementDto;
import com.hotel.management.system.dto.StatementEntryDto;
import com.hotel.management.system.exception.ResourceNotFoundException;
import com.hotel.management.system.model.Booking;
import com.hotel.management.system.model.GlobalCharge;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.model.Transaction;
import com.hotel.management.system.model.TransactionType;
import com.hotel.management.system.repository.GlobalChargeRepository;
import com.hotel.management.system.repository.HotelOwnerRepository;
import com.hotel.management.system.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final HotelOwnerRepository hotelOwnerRepository;
    private final GlobalChargeRepository globalChargeRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              HotelOwnerRepository hotelOwnerRepository,
                              GlobalChargeRepository globalChargeRepository) {
        this.transactionRepository = transactionRepository;
        this.hotelOwnerRepository = hotelOwnerRepository;
        this.globalChargeRepository = globalChargeRepository;
    }

    @Transactional
    public void recordOpeningBalance(HotelOwner owner, BigDecimal openingBalance) {
        Transaction transaction = new Transaction();
        transaction.setOwner(owner);
        transaction.setType(TransactionType.OPENING_BALANCE);
        transaction.setAmount(openingBalance);
        transaction.setDescription("Opening balance assigned");

        transactionRepository.save(transaction);
    }

    @Transactional
    public void recordMonthlyBaseFee(HotelOwner owner, BigDecimal amount, String description, Hotel hotel) {
        owner.setBalance(owner.getBalance().subtract(amount));
        hotelOwnerRepository.save(owner);

        Transaction transaction = new Transaction();
        transaction.setOwner(owner);
        transaction.setHotel(hotel);
        transaction.setType(TransactionType.MONTHLY_BASE_FEE);
        transaction.setAmount(amount.negate());
        transaction.setDescription(description);

        transactionRepository.save(transaction);
    }

    @Transactional
    public void recordMonthlyRoomFee(HotelOwner owner, BigDecimal amount, String description, Room room) {
        owner.setBalance(owner.getBalance().subtract(amount));
        hotelOwnerRepository.save(owner);

        Transaction transaction = new Transaction();
        transaction.setOwner(owner);
        transaction.setHotel(room.getHotel());
        transaction.setType(TransactionType.MONTHLY_ROOM_FEE);
        transaction.setAmount(amount.negate());
        transaction.setDescription(description);

        transactionRepository.save(transaction);
    }

    @Transactional
    public void recordPerTransactionFee(BigDecimal costPerHotel, Hotel hotel,
            Booking booking) {
        HotelOwner owner = hotel.getHotelOwner();
        if (owner == null)
            throw new IllegalStateException("Hotel with ID " + hotel.getHotelId() + " has no associated owner");
        GlobalCharge globalCharges = globalChargeRepository
        .findTopByEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            LocalDate.now()).orElseThrow(() ->
            new IllegalStateException(
                "No global charge configuration found"));
        // Transaction fee percentage is stored as a percentage (e.g., 5 for 5%)
        BigDecimal fee = costPerHotel.multiply(
            globalCharges.getTransactionFeePct().divide(BigDecimal.valueOf(100))
        );
        // Deduct fee from owner's balance and save transaction
        owner.setBalance(owner.getBalance().subtract(fee));
        Transaction transaction = new Transaction(
            owner,
            hotel,
            TransactionType.PER_TRANSACTION_FEE,
            fee.negate(),
            "Per transaction fee for hotel " + hotel.getName(),
            booking
        );
        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public OwnerStatementDto getOwnerStatement(Long ownerId) {
        HotelOwner owner = hotelOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel owner not found with id: " + ownerId));

        List<StatementEntryDto> entries = transactionRepository.findByOwnerUserIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(tx -> new StatementEntryDto(
                        tx.getId(),
                        tx.getType().name(),
                        tx.getAmount(),
                        tx.getDescription(),
                        tx.getCreatedAt(),
                        tx.getHotel() != null ? tx.getHotel().getHotelId() : null,
                        tx.getHotel() != null ? tx.getHotel().getName() : null
                ))
                .toList();

        return new OwnerStatementDto(
                owner.getUserId(),
                owner.getUsername(),
                owner.getEmail(),
                owner.getBalance(),
                entries
        );
    }

    @Transactional(readOnly = true)
    public OwnerStatementDto getOwnerStatementByUsername(String username) {
        HotelOwner owner = hotelOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel owner not found with username: " + username));

        return getOwnerStatement(owner.getUserId());
    }
    @Transactional
    public void deleteByHotel(Long hotelId) {
        transactionRepository.deleteAllByHotelId(hotelId);
    }
}