package com.hotel.management.system.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hotel.management.system.model.GlobalCharge;
import com.hotel.management.system.repository.GlobalChargeRepository;

@Configuration
public class GlobalChargeDataLoader {

    @Bean
    CommandLineRunner seedGlobalCharges(GlobalChargeRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                GlobalCharge charge = new GlobalCharge();
                charge.setBaseMonthlyFee(new BigDecimal("100.00"));
                charge.setPerRoomFee(new BigDecimal("10.00"));
                charge.setTransactionFeePct(new BigDecimal("5.00"));
                charge.setEffectiveFrom(LocalDate.now());

                repository.save(charge);
            }
        };
    }
}