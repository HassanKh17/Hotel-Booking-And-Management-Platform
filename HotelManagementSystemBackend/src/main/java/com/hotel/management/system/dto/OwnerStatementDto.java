package com.hotel.management.system.dto;

import java.math.BigDecimal;
import java.util.List;

public record OwnerStatementDto(
        Long ownerId,
        String username,
        String email,
        BigDecimal currentBalance,
        List<StatementEntryDto> entries
) {
}