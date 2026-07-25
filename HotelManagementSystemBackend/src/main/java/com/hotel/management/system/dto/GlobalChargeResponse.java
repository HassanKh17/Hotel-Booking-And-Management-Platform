package com.hotel.management.system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GlobalChargeResponse {
	private Long id;
	private BigDecimal monthlyBasePrice;
	private BigDecimal perRoomPrice;
	private BigDecimal transactionFeePct;
	private LocalDate effectiveFrom;

	public GlobalChargeResponse(Long id, BigDecimal monthlyBasePrice, BigDecimal perRoomPrice,
			BigDecimal transactionFeePct, LocalDate effectiveFrom) {
		this.id = id;
		this.monthlyBasePrice = monthlyBasePrice;
		this.perRoomPrice = perRoomPrice;
		this.transactionFeePct = transactionFeePct;
		this.effectiveFrom = effectiveFrom;
	}

	public Long getId() {
		return id;
	}

	public BigDecimal getMonthlyBasePrice() {
		return monthlyBasePrice;
	}

	public BigDecimal getPerRoomPrice() {
		return perRoomPrice;
	}

	public BigDecimal getTransactionFeePct() {
		return transactionFeePct;
	}

	public LocalDate getEffectiveFrom() {
		return effectiveFrom;
	}

}