package com.bank.app.presentation.dto.request;

import java.math.BigDecimal;

public record CreateLoanRequest(
        Long customerId,
        BigDecimal amount,
        Integer numberOfInstallments,
        BigDecimal interestRate
) {
}
