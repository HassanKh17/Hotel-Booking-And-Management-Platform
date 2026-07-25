package com.hotel.management.system.service;

import com.hotel.management.system.dto.CustomerDetailsDto;
import com.hotel.management.system.dto.CustomerDto;
import com.hotel.management.system.dto.UpdateCustomerDetailsRequest;
import com.hotel.management.system.exception.DuplicateResourceException;
import com.hotel.management.system.mapper.CustomerMapper;
import com.hotel.management.system.model.Customer;
import com.hotel.management.system.repository.CustomerRepository;
import com.hotel.management.system.repository.UserRepository;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerDto addCustomer(CustomerDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (customerRepository.existsByUsername(dto.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        Customer customer = customerMapper.toEntity(dto);
        Customer saved = customerRepository.save(customer);
        return customerMapper.apply(saved);
    }
    
    @Transactional(readOnly = true)
    public CustomerDetailsDto getMyDetails(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + username));

        return new CustomerDetailsDto(
                customer.getUserId(),
                customer.getUsername(),
                customer.getEmail()
        );
    }

    @Transactional
    public CustomerDetailsDto updateMyDetails(String username, UpdateCustomerDetailsRequest request) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + username));

        if (request.email() != null && !request.email().isBlank()) {
            String newEmail = request.email().trim();

            boolean emailTaken = customerRepository.existsByEmail(newEmail)
                    && !newEmail.equalsIgnoreCase(customer.getEmail());

            if (emailTaken) {
                throw new DuplicateResourceException("Email already exists");
            }

            customer.setEmail(newEmail);
        }

        if (request.password() != null && !request.password().isBlank()) {
            customer.setPassword(passwordEncoder.encode(request.password().trim()));
        }

        userRepository.save(customer);

        return new CustomerDetailsDto(
                customer.getUserId(),
                customer.getUsername(),
                customer.getEmail()
        );
    }
}
