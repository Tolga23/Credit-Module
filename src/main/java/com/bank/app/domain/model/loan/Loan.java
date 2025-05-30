package com.bank.app.domain.model.loan;

import com.bank.app.domain.model.common.InterestRate;
import com.bank.app.domain.model.common.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    public static final Set<Integer> VALID_INSTALLMENTS = Set.of(6, 9, 12, 24);
    public static final int MAX_PAYMENT_MONTHS = 4;

    private Long id;
    private Long customerId;
    private Money loanAmount;
    private Integer numberOfInstallment;
    private InterestRate interestRate;
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();
    @Builder.Default
    private boolean isPaid = false;
    @Builder.Default
    private List<LoanInstallment> installments = new ArrayList<>();

    public static Loan createNewLoan(Long customerId, Money amount, Integer numberOfInstallment, InterestRate interestRate) {
        Loan loan = Loan.builder()
                .customerId(customerId)
                .loanAmount(amount)
                .numberOfInstallment(numberOfInstallment)
                .interestRate(interestRate)
                .build();

        loan.validateLoan();
        return loan;
    }

    public void addInstallments(LoanInstallment installment) {
        installments.add(installment);
        checkAndUpdatePaidStatus();
    }

    public List<LoanInstallment> getInstallments() {
        return Collections.unmodifiableList(installments);
    }

    public Money getTotalLoanAmount() {
        return loanAmount.multiply(BigDecimal.ONE.add(interestRate.getInterestRate()));
    }

    public Money getInstallmentAmount() {
        return getTotalLoanAmount().divide(BigDecimal.valueOf(numberOfInstallment));
    }

    public Money getTotalPaidAmount() {
        return installments.stream()
                .filter(LoanInstallment::isPaid)
                .map(LoanInstallment::getPaidAmount)
                .reduce(Money.ZERO, Money::add);
    }

    public void checkAndUpdatePaidStatus() {
        isPaid = !installments.isEmpty() &&
                installments.stream().allMatch(LoanInstallment::isPaid);
    }

    private void validateLoan() {
        if (customerId == null)
            throw new IllegalArgumentException("Customer ID is required.");

        if (numberOfInstallment == null || !VALID_INSTALLMENTS.contains(numberOfInstallment))
            throw new IllegalArgumentException("Number of installments must be one of: " + VALID_INSTALLMENTS);
    }
}
