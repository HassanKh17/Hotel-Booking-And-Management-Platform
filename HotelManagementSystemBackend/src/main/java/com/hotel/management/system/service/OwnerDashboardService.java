package com.hotel.management.system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.management.system.dto.BookedRoomDto;
import com.hotel.management.system.dto.HotelOccupancyDto;
import com.hotel.management.system.model.BookingItem;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.repository.BookingItemRepository;
import com.hotel.management.system.repository.HotelOwnerRepository;
import com.hotel.management.system.repository.HotelRepository;


@Service
public class OwnerDashboardService {

    private final HotelOwnerRepository ownerRepo;
    private final HotelRepository hotelRepo;
    private final BookingItemRepository bookingItemRepo;

    public OwnerDashboardService(
        HotelOwnerRepository ownerRepo,
        HotelRepository hotelRepo,
        BookingItemRepository bookingItemRepo
    ) {
        this.ownerRepo = ownerRepo;
        this.hotelRepo = hotelRepo;
        this.bookingItemRepo = bookingItemRepo;
    }

    public List<HotelOccupancyDto> getDashboard(String username) {
        HotelOwner owner = ownerRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        List<Hotel> hotels = hotelRepo.findByHotelOwnerUserId(owner.getUserId());
        LocalDate today = LocalDate.now();

        return hotels.stream().map(hotel -> {
            long totalRooms = hotel.getRooms().size();
            long occupiedToday = bookingItemRepo.countOccupiedRoomsToday(hotel.getHotelId(), today);
            long futureBookings = bookingItemRepo.countFutureBookings(hotel.getHotelId(), today);
            long availableToday = totalRooms - occupiedToday;
            double occupancyPercentage = totalRooms == 0 ? 0 : (occupiedToday * 100.0) / totalRooms;

            List<BookedRoomDto> currentBookedRooms = bookingItemRepo
                .findCurrentBookedRoomsByHotel(hotel.getHotelId(), today)
                .stream()
                .map(this::toBookedRoomDto)
                .toList();

            List<BookedRoomDto> upcomingBookedRooms = bookingItemRepo
                .findUpcomingBookedRoomsByHotel(hotel.getHotelId(), today)
                .stream()
                .map(this::toBookedRoomDto)
                .toList();

            return new HotelOccupancyDto(
                hotel.getHotelId(),
                hotel.getName(),
                totalRooms,
                occupiedToday,
                availableToday,
                occupancyPercentage,
                futureBookings,
                currentBookedRooms,
                upcomingBookedRooms
            );
        }).toList();
    }

    private BookedRoomDto toBookedRoomDto(BookingItem item) {
        return new BookedRoomDto(
            item.getId(),
            item.getRoom().getRoomId(),
            item.getRoom().getOccupancyType().name(),
            item.getCheckIn(),
            item.getCheckOut()
        );
    }
}