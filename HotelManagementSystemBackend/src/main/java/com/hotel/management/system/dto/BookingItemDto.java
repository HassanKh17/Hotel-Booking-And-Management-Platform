package com.hotel.management.system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingItemDto(
    Long roomId,
    String hotelName,
    BigDecimal price,
    LocalDate checkIn,
    LocalDate checkOut
) {}
