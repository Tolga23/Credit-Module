package com.bank.app.application.command;

import com.bank.app.domain.model.common.Money;

public record PaymentResult(
        Money totalPaid,
        Money originalAmount,
        int installmentsPaid
) {
}
