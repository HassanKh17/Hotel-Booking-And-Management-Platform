package com.hotel.management.system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressDto(

    @NotNull
    String street,

    @NotNull
    String city,

    @NotNull
    @Pattern(
            regexp = "^[A-Z]{1,2}[0-9][0-9A-Z]? ? [0-9][A-Z]{2}$",
            // regex example - SW1O 6BA
            message = "Must be a Valid UK Postcode (e.g. SW1A 1AA)")
    String postcode
) {}

