package com.bank.app.infrastructure.adapters.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoanInstallmentRequest(
        @NotNull(message = "Customer id is required")
        Long customerId,
        @NotNull(message = "Loan id is required")
        Long loanId
) {
}
