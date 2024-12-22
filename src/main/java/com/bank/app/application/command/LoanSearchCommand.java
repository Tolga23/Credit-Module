package com.bank.app.application.command;

import com.bank.app.presentation.dto.request.LoanSearchRequest;

public record LoanSearchCommand(
        Long customerId,
        Integer numberOfInstallments,
        Boolean isPaid
) {
    public static LoanSearchCommand from(LoanSearchRequest request) {
        return new LoanSearchCommand(
                request.customerId(),
                request.numberOfInstallments(),
                request.isPaid()
        );
    }
}
