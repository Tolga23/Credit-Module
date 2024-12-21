package com.bank.app.presentation.dto.response;

import com.bank.app.domain.model.loan.LoanInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanInstallmentResponse(
        Long id,
        Long loanId,
        BigDecimal amount,
        BigDecimal paidAmount,
        LocalDate dueDate,
        LocalDate paymentDate,
        boolean isPaid
) {
    public static LoanInstallmentResponse from(LoanInstallment installment) {
        if (installment == null) return null;

        return new LoanInstallmentResponse(
                installment.getId(),
                installment.getLoanId(),
                installment.getAmount(),
                installment.getPaidAmount(),
                installment.getDueDate(),
                installment.getPaymentDate(),
                installment.isPaid()
        );
    }
}
