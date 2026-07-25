package com.hotel.management.system.controller;

import com.hotel.management.system.dto.CustomerDetailsDto;
import com.hotel.management.system.dto.CustomerDto;
import com.hotel.management.system.dto.UpdateCustomerDetailsRequest;
import com.hotel.management.system.service.CustomerService;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerDto> register(@RequestBody CustomerDto dto) {
        return new ResponseEntity<>(customerService.addCustomer(dto),
            HttpStatus.CREATED);
    }
    
    @GetMapping("/me")
    public ResponseEntity<CustomerDetailsDto> getMyDetails(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("No authenticated customer found");
        }

        String username = authentication.getName();
        return ResponseEntity.ok(customerService.getMyDetails(username));
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerDetailsDto> updateMyDetails(
            Authentication authentication,
            @RequestBody UpdateCustomerDetailsRequest request
    ) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("No authenticated customer found");
        }

        String username = authentication.getName();
        return ResponseEntity.ok(customerService.updateMyDetails(username, request));
    }

    @GetMapping("/me/cards")
    public ResponseEntity<List<Object>> getMySavedCards() {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/me/cards")
    public ResponseEntity<Map<String, String>> addSavedCard() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Add saved card not implemented yet."));
    }

    @DeleteMapping("/me/cards/{cardId}")
    public ResponseEntity<Map<String, String>> removeSavedCard(@PathVariable Long cardId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Remove saved card not implemented yet."));
    }
}
