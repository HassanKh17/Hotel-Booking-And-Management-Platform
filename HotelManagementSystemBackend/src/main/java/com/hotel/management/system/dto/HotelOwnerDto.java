package com.hotel.management.system.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record HotelOwnerDto(
        Long id,
        @NotNull
        String username,
        @NotNull
        String email,
        @NotNull
        String password,
        BigDecimal balance) {
}
