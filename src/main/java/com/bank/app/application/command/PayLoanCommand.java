package com.bank.app.application.command;

import java.math.BigDecimal;

public record PayLoanCommand(
        Long customerId,
        Long loanId,
        BigDecimal amount
) {
}
