package com.hotel.management.system.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "global_charges", uniqueConstraints = { @UniqueConstraint(columnNames = "effective_from") })
public class GlobalCharge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "charge_id")
	private Long id;

	@Column(name = "base_monthly_fee", nullable = false, precision = 10, scale = 2)
	private BigDecimal baseMonthlyFee;

	@Column(name = "per_room_fee", nullable = false, precision = 10, scale = 2)
	private BigDecimal perRoomFee;

	@Column(name = "transaction_fee_pct", nullable = false, precision = 5, scale = 2)
	private BigDecimal transactionFeePct;

	@Column(name = "effective_from", nullable = false)
	private LocalDate effectiveFrom;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getBaseMonthlyFee() {
		return baseMonthlyFee;
	}

	public void setBaseMonthlyFee(BigDecimal baseMonthlyFee) {
		this.baseMonthlyFee = baseMonthlyFee;
	}

	public BigDecimal getPerRoomFee() {
		return perRoomFee;
	}

	public void setPerRoomFee(BigDecimal perRoomFee) {
		this.perRoomFee = perRoomFee;
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