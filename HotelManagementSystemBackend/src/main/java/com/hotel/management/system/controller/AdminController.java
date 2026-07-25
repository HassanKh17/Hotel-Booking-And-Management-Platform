package com.hotel.management.system.controller;

import com.hotel.management.system.dto.HotelOwnerDto;
import com.hotel.management.system.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/register-owner")
    public ResponseEntity<HotelOwnerDto> registerOwner(@RequestBody HotelOwnerDto dto) {
        return new ResponseEntity<>(adminService.addOwner(dto), HttpStatus.CREATED);
    }

    @PutMapping("/owners/{ownerId}")
    public ResponseEntity<HotelOwnerDto> editOwner(
            @PathVariable Long ownerId,
            @RequestBody HotelOwnerDto dto) {
        return ResponseEntity.ok(adminService.editOwner(ownerId, dto));
    }

    @DeleteMapping("/owners/{ownerId}")
    public ResponseEntity<Void> removeOwner(@PathVariable Long ownerId) {
        adminService.removeOwner(ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owners")
    public ResponseEntity<List<HotelOwnerDto>> viewOwnerOverview() {
        return ResponseEntity.ok(adminService.viewOwnerOverview());
    }

    @PatchMapping("/charges")
    public ResponseEntity<Void> updateGlobalCharges(@RequestParam BigDecimal percentage) {
        adminService.updateGlobalCharges(percentage);
        return ResponseEntity.ok().build();
    }
}
