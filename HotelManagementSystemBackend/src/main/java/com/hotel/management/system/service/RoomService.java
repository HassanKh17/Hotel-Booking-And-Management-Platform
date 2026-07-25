package com.hotel.management.system.service;

import com.hotel.management.system.dto.UnavailableDateRangeDto;
import com.hotel.management.system.exception.HotelNotFound;
import com.hotel.management.system.model.BookingItem;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.repository.BookingItemRepository;
import com.hotel.management.system.repository.HotelRepository;
import com.hotel.management.system.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final BookingItemRepository bookingItemRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository, BookingItemRepository bookingItemRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.bookingItemRepository = bookingItemRepository;
    }

    public Room addRoom(Long hotelId, Room room) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFound("Hotel not found"));

        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    public List<Room> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelHotelId(hotelId);
    }
    
    public List<UnavailableDateRangeDto> getUnavailableDates(Long roomId) {
        List<BookingItem> bookingItems =
                bookingItemRepository.findFutureOrActiveBookingsByRoomId(
                        roomId,
                        LocalDate.now()
                );

        return bookingItems.stream()
                .map(item -> new UnavailableDateRangeDto(
                        item.getCheckIn(),
                        item.getCheckOut()
                ))
                .toList();
    }
}
