package com.hotel.management.system.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hotel.management.system.dto.GlobalChargeRequest;
import com.hotel.management.system.dto.GlobalChargeResponse;
import com.hotel.management.system.service.GlobalChargeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/global-charges")
@Validated
public class GlobalChargeController {

    private final GlobalChargeService globalChargeService;

    public GlobalChargeController(GlobalChargeService globalChargeService) {
        this.globalChargeService = globalChargeService;
    }

    @GetMapping("/current")
    public GlobalChargeResponse getCurrent() {
        return globalChargeService.getCurrent();
    }

    @GetMapping
    public List<GlobalChargeResponse> getAll() {
        return globalChargeService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalChargeResponse create(@Valid @RequestBody GlobalChargeRequest request) {
        return globalChargeService.create(request);
    }
}