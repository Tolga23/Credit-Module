package com.bank.app.infrastructure.adapters.in.rest.dto.response;

import com.bank.app.domain.model.loan.PaymentResult;

import java.math.BigDecimal;

public record PayLoanResponse(
        BigDecimal totalPaid,
        int installmentsPaid,
        boolean loanFullyPaid
) {
    public static PayLoanResponse from(PaymentResult result) {
        if (result == null) {
            return null;
        }

        return new PayLoanResponse(
                result.totalPaid().getValue(),
                result.installmentsPaid(),
                result.loanFullyPaid()
        );
    }
}

