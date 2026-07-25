package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.CustomerDto;
import com.hotel.management.system.model.Customer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerMapper implements Function<Customer, CustomerDto> {

    private final PasswordEncoder passwordEncoder;

    public CustomerMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerDto apply(Customer customer) {
        return new CustomerDto(
                customer.getUserId(),
                customer.getUsername(),
                customer.getEmail(),
                null);
    }

    public Customer toEntity(CustomerDto dto) {
        return new Customer(dto.username(), dto.email(),
            // Hash the password before mapping to entity
            passwordEncoder.encode(dto.password()));
    }
}
