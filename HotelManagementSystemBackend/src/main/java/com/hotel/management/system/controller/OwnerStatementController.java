package com.hotel.management.system.controller;

import com.hotel.management.system.dto.OwnerStatementDto;
import com.hotel.management.system.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel-owner")
public class OwnerStatementController {

    private final TransactionService transactionService;

    public OwnerStatementController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/statement")
    public ResponseEntity<OwnerStatementDto> getMyStatement(Authentication authentication) {
        String username = authentication.getName();

        return ResponseEntity.ok(
            transactionService.getOwnerStatementByUsername(username)
        );
    }
}