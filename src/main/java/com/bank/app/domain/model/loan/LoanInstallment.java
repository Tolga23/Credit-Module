package com.bank.app.domain.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {
    private Long id;
    private Long loanId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    @Builder.Default
    private boolean isPaid = false;

    public void validateInstallment(){
        if (loanId == null)
            throw new IllegalArgumentException("Load ID is required.");

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be positive");

        if (dueDate == null || dueDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("DueDate cannot be in the past or null.");
    }
}
