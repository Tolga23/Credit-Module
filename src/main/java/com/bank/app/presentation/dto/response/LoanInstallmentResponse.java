package com.bank.app.presentation.dto.response;

import com.bank.app.domain.model.loan.LoanInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<LoanInstallmentResponse> from(List<LoanInstallment> installment) {
        if (installment == null) return null;

        return installment.stream()
                .map(LoanInstallmentResponse::from)
                .collect(Collectors.toList());
    }
}
