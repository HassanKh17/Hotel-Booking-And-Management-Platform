package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.BookingItemDto;
import com.hotel.management.system.dto.BookingResponseDto;
import com.hotel.management.system.model.Booking;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class BookingMapper implements Function<Booking, BookingResponseDto> {

    private final BookingItemMapper itemMapper;

    public BookingMapper(BookingItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public BookingResponseDto apply(Booking booking) {
        // Convert the list of BookingItem entities into BookingItemDtos
        List<BookingItemDto> itemDtos = booking.getItems().stream()
                .map(itemMapper::apply)
                .toList();

        return new BookingResponseDto(
                booking.getId(),
                booking.getCustomer().getUserId(),
                booking.getTotalCost(),
                booking.getCreatedAt(),
                itemDtos
        );
    }
}