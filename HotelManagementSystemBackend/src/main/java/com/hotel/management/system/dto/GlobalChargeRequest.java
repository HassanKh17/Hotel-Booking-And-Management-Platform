package com.hotel.management.system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class GlobalChargeRequest {

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal monthlyBasePrice;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal perRoomPrice;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 5, fraction = 2)
    private BigDecimal transactionFeePct;

    @NotNull
    private LocalDate effectiveFrom;

	public BigDecimal getMonthlyBasePrice() {
		return monthlyBasePrice;
	}

	public void setMonthlyBasePrice(BigDecimal monthlyBasePrice) {
		this.monthlyBasePrice = monthlyBasePrice;
	}

	public BigDecimal getPerRoomPrice() {
		return perRoomPrice;
	}

	public void setPerRoomPrice(BigDecimal perRoomPrice) {
		this.perRoomPrice = perRoomPrice;
	}

	public BigDecimal getTransactionFeePct() {
		return transactionFeePct;
	}

	public void setTransactionFeePct(BigDecimal transactionFeePct) {
		this.transactionFeePct = transactionFeePct;
	}

	public LocalDate getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(LocalDate effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
    
    
}