package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.AddressDto;
import com.hotel.management.system.model.Address;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AddressDtoMapper implements Function<Address, AddressDto> {

    public Address toEntity(AddressDto dto) {
        return new Address(dto.street(), dto.city(), dto.postcode());
    }

    public AddressDto apply(Address address) {
        return new AddressDto(
                address.getStreet(),
                address.getCity(),
                address.getPostcode()
        );
    }
}
