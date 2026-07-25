package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.HotelOwnerDto;
import com.hotel.management.system.model.HotelOwner;

import java.math.BigDecimal;
import java.util.function.Function;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class HotelOwnerMapper implements Function<HotelOwner, HotelOwnerDto> {

    private final PasswordEncoder passwordEncoder;

    public HotelOwnerMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public HotelOwnerDto apply(HotelOwner hotelOwner) {
        return new HotelOwnerDto(
                hotelOwner.getUserId(),
                hotelOwner.getUsername(),
                hotelOwner.getEmail(),
                null,
                hotelOwner.getBalance());
    }

    public HotelOwner toEntity(HotelOwnerDto dto) {
        HotelOwner owner = new HotelOwner();
        owner.setUsername(dto.username());
        owner.setEmail(dto.email());
        // Hash the password before mapping
        owner.setPassword(passwordEncoder.encode(dto.password()));
        owner.setBalance(dto.balance() != null ? dto.balance() : BigDecimal.ZERO);
        return owner;
    }

    public void updateEntity(HotelOwner owner, HotelOwnerDto dto) {
        if (dto.username() != null && !dto.username().isBlank()) {
            owner.setUsername(dto.username());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            owner.setEmail(dto.email());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            // Hash the password before updating
            owner.setPassword(passwordEncoder.encode(dto.password()));
        }

        if (dto.balance() != null) {
            owner.setBalance(dto.balance());
        }
    }
}
