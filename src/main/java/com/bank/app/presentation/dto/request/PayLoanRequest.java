package com.bank.app.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PayLoanRequest(
        @NotNull(message = "Customer id is required")
        Long customerId,
        @NotNull(message = "Loan id is required")
        Long loanId,
        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.0", message = "Payment amount must be greater than 0")
        BigDecimal amount
) {
}
