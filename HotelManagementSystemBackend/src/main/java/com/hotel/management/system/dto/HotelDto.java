package com.hotel.management.system.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record HotelDto(
        Long hotelId,

        @NotNull
        String name,
        String description,
        Integer starRating,
        String photoUrl,
        BigDecimal specialOfferPercentage,

        @NotNull
        AddressDto address,
        @NotNull
        List<RoomDto> rooms,
        @NotNull
        List<FacilityDto> facilities,
        @NotNull
        List<AmenityDto> amenities,
        String ownerUsername
) {}
