package com.bank.app.presentation.dto.request;

public record LoanSearchRequest(
        Integer numberOfInstallments,
        Boolean isPaid
) {
}
