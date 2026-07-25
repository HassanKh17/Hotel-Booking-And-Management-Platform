package com.hotel.management.system.dto;

import java.util.List;

public record CustomerBookingsDto(
    List<BookingResponseDto> currentBookings,
    List<BookingResponseDto> previousBookings
) {}