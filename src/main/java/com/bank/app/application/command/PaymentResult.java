package com.bank.app.application.command;

import java.math.BigDecimal;

public record PaymentResult(
        BigDecimal totalPaid,
        BigDecimal originalAmount,
        int installmentsPaid
) {
}
