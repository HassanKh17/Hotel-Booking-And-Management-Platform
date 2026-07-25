package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.BookingItemDto;
import com.hotel.management.system.model.BookingItem;
import com.hotel.management.system.model.Room;
import org.springframework.stereotype.Service;

@Service
public class BookingItemMapper {

    public BookingItem toEntity(BookingItemDto dto, Room room) {
        return new BookingItem(room, dto.price(), dto.checkIn(), dto.checkOut()
        );
    }

    public BookingItemDto apply(BookingItem item) {
        return new BookingItemDto(
                item.getRoom().getRoomId(),
                item.getRoom().getHotel().getName(),
                item.getPrice(),
                item.getCheckIn(),
                item.getCheckOut()
        );
    }
}
