package com.hotel.management.system.dto;

public record UpdateCustomerDetailsRequest(
        String email,
        String password
) {
}