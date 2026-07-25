package com.hotel.management.system.controller;

import com.hotel.management.system.dto.OwnerStatementDto;
import com.hotel.management.system.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/owners")
public class AdminStatementController {

    private final TransactionService transactionService;

    public AdminStatementController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{ownerId}/statement")
    public ResponseEntity<OwnerStatementDto> getOwnerStatement(@PathVariable Long ownerId) {
        return ResponseEntity.ok(transactionService.getOwnerStatement(ownerId));
    }
}