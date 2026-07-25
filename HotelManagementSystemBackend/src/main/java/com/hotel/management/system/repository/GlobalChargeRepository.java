package com.hotel.management.system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.management.system.model.GlobalCharge;

@Repository
public interface GlobalChargeRepository extends JpaRepository<GlobalCharge, Long> {

    Optional<GlobalCharge> findTopByEffectiveFromLessThanEqualOrderByEffectiveFromDesc(LocalDate date);

    List<GlobalCharge> findAllByOrderByEffectiveFromDesc();
    
    boolean existsByEffectiveFrom(LocalDate effectiveFrom);
}