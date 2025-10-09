package com.bank.app.application.command;

public record LoanSearchCommand(
        Long customerId,
        Integer numberOfInstallments,
        Boolean isPaid
) {
}
