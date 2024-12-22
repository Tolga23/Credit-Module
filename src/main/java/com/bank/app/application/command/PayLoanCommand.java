package com.bank.app.application.command;

import com.bank.app.presentation.dto.request.PayLoanRequest;

import java.math.BigDecimal;

public record PayLoanCommand(
        Long customerId,
        Long loanId,
        BigDecimal amount
) {
    public static PayLoanCommand from(PayLoanRequest request) {
        return new PayLoanCommand(
                request.customerId(),
                request.loanId(),
                request.amount());
    }
}
