package com.bank.app.application.command;

import com.bank.app.infrastructure.adapters.in.rest.dto.request.CreateLoanRequest;

import java.math.BigDecimal;

public record CreateLoanCommand(
        Long customerId,
        BigDecimal amount,
        Integer numberOfInstallments,
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
