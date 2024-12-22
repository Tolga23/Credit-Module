package com.bank.app.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoanSearchRequest(
        @NotNull(message = "Customer id is required")
        Long customerId,
        Integer numberOfInstallments,
        Boolean isPaid
) {
}
