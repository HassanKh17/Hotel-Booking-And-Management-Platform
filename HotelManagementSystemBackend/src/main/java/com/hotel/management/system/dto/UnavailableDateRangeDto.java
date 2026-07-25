package com.hotel.management.system.dto;

import java.time.LocalDate;

public record UnavailableDateRangeDto(
    LocalDate checkIn,
    LocalDate checkOut
) {}