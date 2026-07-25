package com.hotel.management.system.controller;

import com.hotel.management.system.dto.BookingDto;
import com.hotel.management.system.dto.BookingResponseDto;
import com.hotel.management.system.dto.CustomerBookingsDto;
import com.hotel.management.system.service.BookingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
        @RequestBody BookingDto bookingDto, 
        Authentication authentication
    ) {
        // Extract username from authentication context
        String username = getAuthenticatedUsername(authentication);
        BookingResponseDto booking = bookingService.addBooking(bookingDto,
            username);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }
    
    @GetMapping("/me")
    public ResponseEntity<CustomerBookingsDto> getMyBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getMyBookings(getAuthenticatedUsername(authentication)));
    }

    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        if (authentication == null) {
            throw new AccessDeniedException("Authentication is required");
        }

        return authentication.getName();
    }
}
