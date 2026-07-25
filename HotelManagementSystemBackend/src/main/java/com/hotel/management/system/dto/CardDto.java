package com.hotel.management.system.dto;

public record CardDto(
    String cardholderName,
    String cardNumber,
    String expiryDate,
    AddressDto billingAddress
) {}
