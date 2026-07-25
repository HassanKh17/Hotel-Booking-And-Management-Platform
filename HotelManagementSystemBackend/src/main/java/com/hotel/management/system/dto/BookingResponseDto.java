package com.hotel.management.system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookingResponseDto(
    Long bookingId,
    Long customerId,
    BigDecimal totalCost,
    LocalDate createdAt,
    List<BookingItemDto> items
) {}
