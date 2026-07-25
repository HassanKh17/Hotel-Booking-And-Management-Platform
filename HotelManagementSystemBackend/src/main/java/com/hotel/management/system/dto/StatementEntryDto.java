package com.hotel.management.system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StatementEntryDto(
        Long id,
        String type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt,
        Long hotelId,
        String hotelName
) {
}