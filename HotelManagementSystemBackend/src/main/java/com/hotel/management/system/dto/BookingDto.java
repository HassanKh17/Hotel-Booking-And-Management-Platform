package com.hotel.management.system.dto;

import java.util.List;

public record BookingDto(
    // Nullable: if present, we ignore the manual fields below
    Long savedCardId, 
    // Manual fields
    String cardholderName,
    String cardNumber,
    String expiry,
    AddressDto address,
    
    List<BookingItemDto> items
) {}
