package com.bank.app.domain.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private Long id;
    private Long customerId;
    private BigDecimal loanAmount;
    private int numberOfInstallment;
    private LocalDateTime createdDate;
    private boolean isPaid;
}
