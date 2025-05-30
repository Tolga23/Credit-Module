package com.bank.app.domain.model.loan;

import com.bank.app.domain.model.common.InstallmentCount;
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

    public static final int MAX_PAYMENT_MONTHS = 4;

    private Long id;
    private Long customerId;
    private Money loanAmount;
    private InstallmentCount numberOfInstallment;
    private InterestRate interestRate;
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();
    @Builder.Default
    private boolean isPaid = false;
    @Builder.Default
    private List<LoanInstallment> installments = new ArrayList<>();

    public static Loan createNewLoan(Long customerId, Money amount, InstallmentCount numberOfInstallment, InterestRate interestRate) {
        return Loan.builder()
                .customerId(customerId)
                .loanAmount(amount)
                .numberOfInstallment(numberOfInstallment)
                .interestRate(interestRate)
                .build();
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
        return getTotalLoanAmount().divide(BigDecimal.valueOf(numberOfInstallment.getNumberOfInstallment()));
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
}
