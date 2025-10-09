package com.bank.app.domain.model.loan;

import com.bank.app.domain.model.common.Money;

public record PaymentResult(
        Money totalPaid,
        Money originalAmount,
        int installmentsPaid,
        boolean loanFullyPaid
) {
}
