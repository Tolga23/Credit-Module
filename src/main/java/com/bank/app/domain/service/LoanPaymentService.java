package com.bank.app.domain.service;

import com.bank.app.domain.model.loan.PaymentResult;
import com.bank.app.domain.exception.UnauthorizedCustomerException;
import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanPaymentPort;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class LoanPaymentService implements LoanPaymentPort {

    @Override
    public PaymentResult processPayment(Loan loan, Long customerId, Money paymentAmount) {
        validateLoanForPayment(loan);
        validateOwnership(loan, customerId);

        LocalDate paymentDate = LocalDate.now();
        List<LoanInstallment> eligibleInstallments = getEligibleInstallments(loan, paymentDate);
        validatePaymentAmount(eligibleInstallments.get(0), paymentAmount);
        return processInstallmentPayments(eligibleInstallments, paymentAmount, paymentDate, loan);
    }

    private PaymentResult processInstallmentPayments(List<LoanInstallment> eligibleInstallments,
                                                     Money amount,
                                                     LocalDate paymentDate,
                                                     Loan loan) {
        Money remainingAmount = amount;
        Money totalPaid = Money.ZERO;
        Money originalAmount = Money.ZERO;
        int installmentsPaid = 0;

        for (LoanInstallment installment : eligibleInstallments) {
            Money requiredAmount = installment.calculatePaymentAmount(paymentDate);

            if (remainingAmount.compareTo(requiredAmount) >= 0) {
                originalAmount = originalAmount.add(installment.getAmount());
                installment.processPayment(remainingAmount, requiredAmount);
                remainingAmount = remainingAmount.subtract(requiredAmount);
                totalPaid = totalPaid.add(requiredAmount);
                installmentsPaid++;
            } else {
                break;
            }
        }

        loan.checkAndUpdatePaidStatus();
        return new PaymentResult(totalPaid, originalAmount, installmentsPaid, loan.isPaid());
    }

    public void validateOwnership(Loan loan, Long customerId) {
        if (!loan.getCustomerId().equals(customerId)) {
            throw new UnauthorizedCustomerException("You do not have permission to access this loan");
        }

        if (loan.isPaid()) {
            throw new IllegalStateException("Loan is already paid.");
        }
    }

    private void validatePaymentAmount(LoanInstallment firstInstallment, Money amount) {
        Money requiredAmount = firstInstallment.getAmount();
        if (amount.compareTo(requiredAmount) < 0) {
            throw new IllegalArgumentException("Payment amount must be at least: " + requiredAmount);
        }
    }

    private List<LoanInstallment> getEligibleInstallments(Loan loan, LocalDate paymentDate) {
        List<LoanInstallment> eligibleInstallments = loan.getInstallments().stream()
                .filter(installment -> !installment.isPaid())
                .filter(installment -> !installment.getDueDate()
                        .isAfter(paymentDate.plusMonths(Loan.MAX_PAYMENT_MONTHS)))
                .sorted(Comparator.comparing(LoanInstallment::getDueDate))
                .toList();

        if (eligibleInstallments.isEmpty()) {
            throw new IllegalStateException("No eligible installments found for payment.");
        }

        return eligibleInstallments;
    }

    private void validateLoanForPayment(Loan loan) {
        if (loan.isPaid()) {
            throw new IllegalStateException("Loan is already paid.");
        }
        if (loan.getInstallments().isEmpty()) {
            throw new IllegalStateException("Loan has no installments.");
        }
    }
}




