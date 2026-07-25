package com.hotel.management.system.dto;

public record CustomerDto(
        Long id,
        String username,
        String email,
        String password) {
}