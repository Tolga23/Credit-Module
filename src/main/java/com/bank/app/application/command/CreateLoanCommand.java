package com.bank.app.application.command;

import java.math.BigDecimal;

public record CreateLoanCommand(
        Long customerId,
        BigDecimal amount,
        Integer numberOfInstallments,
        BigDecimal interestRate
) {
}
