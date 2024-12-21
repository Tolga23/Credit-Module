package com.bank.app.domain.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    public static final Set<Integer> VALID_INSTALLMENTS = Set.of(6, 9, 12, 24);
    public static final BigDecimal MIN_INTEREST_RATE = BigDecimal.valueOf(0.1);
    public static final BigDecimal MAX_INTEREST_RATE = BigDecimal.valueOf(0.5);

    private Long id;
    private Long customerId;
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private BigDecimal interestRate;
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();
    @Builder.Default
    private boolean isPaid = false;
    @Builder.Default
    private List<LoanInstallment> installments = new ArrayList<>();

    public void addInstallments(LoanInstallment installment){
        installments.add(installment);
    }

    public void removeInstallments(LoanInstallment installment){
        installments.remove(installment);
    }

    public BigDecimal getTotalLoanAmount() {
        return loanAmount.multiply(BigDecimal.ONE.add(interestRate));
    }

    public BigDecimal getInstallmentAmount(){
        return getTotalLoanAmount().divide(BigDecimal.valueOf(numberOfInstallment));
    }

    public void validateLoan() {
        if (customerId == null)
            throw new IllegalArgumentException("Customer ID is required.");

        if (loanAmount == null || loanAmount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Loan amount is required and should be greater than zero.");

        if (numberOfInstallment == null || !VALID_INSTALLMENTS.contains(numberOfInstallment))
            throw new IllegalArgumentException("Number of installments must be one of: " + VALID_INSTALLMENTS);

        if (interestRate == null || interestRate.compareTo(MAX_INTEREST_RATE) < 0 || interestRate.compareTo(MAX_INTEREST_RATE) > 0)
            throw new IllegalArgumentException("Interest rate must be between  " + MIN_INTEREST_RATE + " and " + MAX_INTEREST_RATE);
    }
}
