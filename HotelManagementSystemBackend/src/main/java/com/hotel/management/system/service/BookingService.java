package com.hotel.management.system.service;

import com.hotel.management.system.dto.BookingDto;
import com.hotel.management.system.dto.BookingItemDto;
import com.hotel.management.system.dto.BookingResponseDto;
import com.hotel.management.system.dto.CustomerBookingsDto;
import com.hotel.management.system.mapper.BookingItemMapper;
import com.hotel.management.system.mapper.BookingMapper;
import com.hotel.management.system.model.*;
import com.hotel.management.system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {

    private final SavedCardService cardService;
    private final CustomerRepository customerRepo;
    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepo;
    private final BookingItemMapper bookingItemMapper;
    private final BookingMapper bookingMapper;
    private final TransactionService transactionService;
    private final BookingItemRepository bookingItemRepo;
    private final BookingEmailService bookingEmailService;

    BookingService(
        SavedCardService cardService,
            CustomerRepository customerRepo,
            RoomRepository roomRepo,
            BookingRepository bookingRepo,
            BookingItemMapper bookingItemMapper,
            BookingMapper bookingMapper,
            TransactionService transactionService,
            BookingItemRepository bookingItemRepo,
            BookingEmailService bookingEmailService) {
        this.cardService = cardService;
        this.customerRepo = customerRepo;
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
        this.bookingItemMapper = bookingItemMapper;
        this.bookingMapper = bookingMapper;
        this.transactionService = transactionService;
        this.bookingItemRepo= bookingItemRepo;
        this.bookingEmailService = bookingEmailService;
    }

    @Transactional
    public BookingResponseDto addBooking(BookingDto bookingDto, String username) {
        if (bookingDto.items() == null || bookingDto.items().isEmpty())
            throw new IllegalArgumentException(
                    "Booking must contain at least one item");
        // Fetch customer by username
        Customer customer = customerRepo.findByUsername(username)
            .orElseThrow(() ->
                new IllegalArgumentException(username + " not found"));
                
        // Persist booking with a bogus total cost
        Booking booking = new Booking(customer, BigDecimal.ZERO,
                LocalDate.now());
        BigDecimal totalCost = BigDecimal.ZERO;
        // A transaction fee is recorded per hotel
        Map<Hotel, BigDecimal> totalCostPerHotel = new HashMap<>();

        List<BookingItemDto> sortedItems = bookingDto.items().stream()
                .sorted(Comparator.comparing(BookingItemDto::roomId))
                .toList();

        for (BookingItemDto itemDto : sortedItems) {
            validateBookingItem(itemDto);

            Room room = roomRepo.findByIdForUpdate(itemDto.roomId())
                    .orElseThrow(() -> new IllegalArgumentException("Room with ID " + itemDto.roomId() + " not found"));
            
            Hotel hotel = room.getHotel();
            if (hotel == null)
                throw new IllegalStateException("Room with ID " + room.getRoomId() + " has no associated hotel");


            boolean conflict = bookingItemRepo.existsOverlappingBooking(
                    room.getRoomId(),
                    itemDto.checkIn(),
                    itemDto.checkOut());

            if (conflict) {
                throw new IllegalStateException(
                        "Room with ID " + room.getRoomId() + " is no longer available for the selected dates");
            }
            
            long nights = ChronoUnit.DAYS.between(itemDto.checkIn(), itemDto.checkOut());
            if (nights <= 0) {
            	throw new IllegalArgumentException("Check-out date must be after check-in date");
            
            }
            
            BigDecimal itemTotal = calculateDiscountedPrice(room)
                    .multiply(BigDecimal.valueOf(nights));

            totalCost = totalCost.add(itemTotal);
            totalCostPerHotel.merge(hotel, itemTotal, BigDecimal::add);

            BookingItem bookingItem = bookingItemMapper.toEntity(itemDto, room);
            bookingItem.setPrice(itemTotal);
            booking.addBookingItem(bookingItem);
       
        }

        booking.setTotalCost(totalCost);
        booking = bookingRepo.save(booking);
        // Record the per hotel transaction fee
        for (Map.Entry<Hotel, BigDecimal> entry : totalCostPerHotel.entrySet()) {
            transactionService.recordPerTransactionFee(entry.getValue(),
                    entry.getKey(), booking);
        }
        bookingEmailService.sendBookingConfirmation(booking);
        // Map the saved booking to a DTO and return
        return bookingMapper.apply(booking);
    }
    
    @Transactional(readOnly = true)
    public CustomerBookingsDto getMyBookings(String username) {
        Customer customer = customerRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + username));

        List<Booking> bookings = bookingRepo.findByCustomerOrderByCreatedAtDesc(customer);

        LocalDate today = LocalDate.now();
        List<BookingResponseDto> currentBookings = new ArrayList<>();
        List<BookingResponseDto> previousBookings = new ArrayList<>();

        for (Booking booking : bookings) {
            BookingResponseDto dto = bookingMapper.apply(booking);

            LocalDate latestCheckOut = booking.getItems().stream()
                    .map(BookingItem::getCheckOut)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            if (latestCheckOut != null && !latestCheckOut.isBefore(today)) {
                currentBookings.add(dto);
            } else {
                previousBookings.add(dto);
            }
        }

        return new CustomerBookingsDto(currentBookings, previousBookings);
    }

    private void validateBookingItem(BookingItemDto itemDto) {
        if (itemDto.roomId() == null) {
            throw new IllegalArgumentException("Room ID is required");
        }

        if (itemDto.checkIn() == null || itemDto.checkOut() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }

        if (!itemDto.checkIn().isBefore(itemDto.checkOut())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        if (itemDto.checkIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

    }

    private BigDecimal calculateDiscountedPrice(Room room) {
        BigDecimal originalPrice = room.getPricePerNight();
        if (originalPrice == null) {
            throw new IllegalArgumentException("Room price is required");
        }

        Hotel hotel = room.getHotel();
        BigDecimal specialOfferPercentage = hotel == null || hotel.getSpecialOfferPercentage() == null
                ? BigDecimal.ZERO
                : hotel.getSpecialOfferPercentage();

        if (specialOfferPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return originalPrice;
        }

        BigDecimal discountMultiplier = BigDecimal.valueOf(100)
                .subtract(specialOfferPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
