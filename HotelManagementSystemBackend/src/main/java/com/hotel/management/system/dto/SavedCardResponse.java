package com.hotel.management.system.dto;

public record SavedCardResponse(
    Long savedCardId,
    String cardholderName,
    String lastFour,
    String expiryDate
) {}
