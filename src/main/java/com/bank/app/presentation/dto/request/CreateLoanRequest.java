package com.bank.app.presentation.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateLoanRequest(
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Loan amount is required")
        BigDecimal amount,

        @NotNull(message = "Number of installments is required")
        Integer numberOfInstallments,

        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1")
        @DecimalMax(value = "0.5", message = "Interest rate must be at most 0.5")
        @Digits(integer = 1, fraction = 2, message = "Interest rate must have at most 2 decimal places")
        BigDecimal interestRate
) {
}
