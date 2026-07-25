package com.hotel.management.system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.management.system.dto.GlobalChargeRequest;
import com.hotel.management.system.dto.GlobalChargeResponse;
import com.hotel.management.system.model.GlobalCharge;
import com.hotel.management.system.repository.GlobalChargeRepository;

@Service
public class GlobalChargeService {

    private final GlobalChargeRepository globalChargeRepository;

    public GlobalChargeService(GlobalChargeRepository globalChargeRepository) {
        this.globalChargeRepository = globalChargeRepository;
    }

    @Transactional
    public GlobalChargeResponse create(GlobalChargeRequest request) {
    	 if (globalChargeRepository.existsByEffectiveFrom(request.getEffectiveFrom())) {
             throw new IllegalArgumentException(
                 "A global charge record already exists for effective date: " + request.getEffectiveFrom()
             );
         }
    	GlobalCharge charge = new GlobalCharge();
        charge.setBaseMonthlyFee(request.getMonthlyBasePrice());
        charge.setPerRoomFee(request.getPerRoomPrice());
        charge.setTransactionFeePct(request.getTransactionFeePct());
        charge.setEffectiveFrom(request.getEffectiveFrom());

        GlobalCharge saved = globalChargeRepository.save(charge);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public GlobalChargeResponse getCurrent() {
        GlobalCharge charge = globalChargeRepository
                .findTopByEffectiveFromLessThanEqualOrderByEffectiveFromDesc(LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("No active global charges found."));

        return toResponse(charge);
    }

    @Transactional(readOnly = true)
    public List<GlobalChargeResponse> getAll() {
        return globalChargeRepository.findAllByOrderByEffectiveFromDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private GlobalChargeResponse toResponse(GlobalCharge charge) {
        return new GlobalChargeResponse(
                charge.getId(),
                charge.getBaseMonthlyFee(),
                charge.getPerRoomFee(),
                charge.getTransactionFeePct(),
                charge.getEffectiveFrom()
        );
    }
}