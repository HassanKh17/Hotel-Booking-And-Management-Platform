package com.hotel.management.system.service;

import com.hotel.management.system.dto.BookingDto;
import com.hotel.management.system.dto.BookingItemDto;
import com.hotel.management.system.mapper.BookingItemMapper;
import com.hotel.management.system.mapper.BookingMapper;
import com.hotel.management.system.model.Booking;
import com.hotel.management.system.model.Customer;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.OccupancyStatus;
import com.hotel.management.system.model.OccupancyType;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.repository.BookingItemRepository;
import com.hotel.management.system.repository.BookingRepository;
import com.hotel.management.system.repository.CustomerRepository;
import com.hotel.management.system.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private SavedCardService cardService;
    @Mock
    private CustomerRepository customerRepo;
    @Mock
    private RoomRepository roomRepo;
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private TransactionService transactionService;
    @Mock
    private BookingItemRepository bookingItemRepo;
    @Mock
    private BookingEmailService bookingEmailService;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        BookingItemMapper itemMapper = new BookingItemMapper();
        bookingService = new BookingService(
                cardService,
                customerRepo,
                roomRepo,
                bookingRepo,
                itemMapper,
                new BookingMapper(itemMapper),
                transactionService,
                bookingItemRepo,
                bookingEmailService);
    }

    @Test
    void addBooking_usesOriginalRoomPrice_whenNoSpecialOffer() {
        Room room = buildRoom("100.00", BigDecimal.ZERO);
        BookingDto request = buildBookingRequest("1.00");

        mockBookingDependencies(room);

        bookingService.addBooking(request, "guest");

        Booking savedBooking = captureSavedBooking();
        assertThat(savedBooking.getTotalCost()).isEqualByComparingTo("200.00");
        assertThat(savedBooking.getItems().get(0).getPrice()).isEqualByComparingTo("200.00");
        verify(bookingEmailService).sendBookingConfirmation(savedBooking);
    }

    @Test
    void addBooking_appliesSpecialOfferAndIgnoresClientPrice() {
        Room room = buildRoom("100.00", new BigDecimal("10.00"));
        BookingDto request = buildBookingRequest("9999.00");

        mockBookingDependencies(room);

        bookingService.addBooking(request, "guest");

        Booking savedBooking = captureSavedBooking();
        assertThat(savedBooking.getTotalCost()).isEqualByComparingTo("180.00");
        assertThat(savedBooking.getItems().get(0).getPrice()).isEqualByComparingTo("180.00");
        verify(bookingEmailService).sendBookingConfirmation(savedBooking);
    }

    private void mockBookingDependencies(Room room) {
        when(customerRepo.findByUsername(anyString()))
                .thenReturn(Optional.of(new Customer("guest", "guest@test.com", "password")));
        when(roomRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(bookingItemRepo.existsOverlappingBooking(any(), any(), any())).thenReturn(false);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private BookingDto buildBookingRequest(String clientPrice) {
        return new BookingDto(
                null,
                "Guest",
                "4111111111111111",
                "12/30",
                null,
                List.of(new BookingItemDto(
                        1L,
                        "Test Hotel",
                        new BigDecimal(clientPrice),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3))));
    }

    private Room buildRoom(String pricePerNight, BigDecimal specialOfferPercentage) {
        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);
        hotel.setName("Test Hotel");
        hotel.setSpecialOfferPercentage(specialOfferPercentage);

        Room room = new Room();
        room.setRoomId(1L);
        room.setOccupancyType(OccupancyType.DOUBLE);
        room.setOccupancyStatus(OccupancyStatus.AVAILABLE);
        room.setCapacity(2);
        room.setPricePerNight(new BigDecimal(pricePerNight));
        room.setHotel(hotel);

        return room;
    }

    private Booking captureSavedBooking() {
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepo).save(bookingCaptor.capture());
        return bookingCaptor.getValue();
    }
}
