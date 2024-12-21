package com.bank.app.domain.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private Long id;
    private Long customerId;
    private BigDecimal loanAmount;
    private int numberOfInstallment;
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();
    @Builder.Default
    private boolean isPaid = false;
    @Builder.Default
    private List<LoanInstallment> installments = new ArrayList<>();
}
