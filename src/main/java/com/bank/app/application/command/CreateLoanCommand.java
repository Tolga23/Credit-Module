package com.bank.app.application.command;

import com.bank.app.presentation.dto.request.CreateLoanRequest;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateLoanCommand(
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Loan amount is required")
        BigDecimal amount,

        @NotNull(message = "Number of installments is required")
        Integer numberOfInstallments,

        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1")
        @DecimalMax(value = "0.5", message = "Interest rate must be at most 0.5")
        BigDecimal interestRate
) {
    public static CreateLoanCommand from(CreateLoanRequest request) {
        return new CreateLoanCommand(
                request.customerId(),
                request.amount(),
                request.numberOfInstallments(),
                request.interestRate());
    }
}
