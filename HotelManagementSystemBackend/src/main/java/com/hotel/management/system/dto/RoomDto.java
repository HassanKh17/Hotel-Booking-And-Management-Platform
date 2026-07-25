package com.hotel.management.system.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoomDto(

        Long roomId,

        String occupancyType,

        @NotNull
        int numberOfRooms,

        String occupancyStatus,
        
        @NotNull
        int capacity,

        @NotNull
        BigDecimal pricePerNight,
        BigDecimal discountedPricePerNight,
        BigDecimal specialOfferPercentage,
        boolean specialOfferApplied
) {}
