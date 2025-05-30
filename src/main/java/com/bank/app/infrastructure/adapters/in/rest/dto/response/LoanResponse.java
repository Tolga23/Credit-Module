package com.bank.app.infrastructure.adapters.in.rest.dto.response;

import com.bank.app.domain.model.loan.Loan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record LoanResponse(
        Long id,
        Long customerId,
        BigDecimal loanAmount,
        Integer numberOfInstallment,
        LocalDateTime createdDate,
        boolean isPaid,
        List<LoanInstallmentResponse> installments
) {
    public static LoanResponse from(Loan loan) {
        if (loan == null) return null;

        return new LoanResponse(
                loan.getId(),
                loan.getCustomerId(),
                loan.getLoanAmount().getValue(),
                loan.getNumberOfInstallment(),
                loan.getCreatedDate(),
                loan.isPaid(),
                loan.getInstallments().stream()
                        .map(LoanInstallmentResponse::from)
                        .collect(Collectors.toList())
        );
    }

    public static List<LoanResponse> from(List<Loan> loans) {
        if (loans == null) return null;
        return loans.stream()
                .map(LoanResponse::from)
                .collect(Collectors.toList());
    }
}
