package com.bank.app.domain.service;

import com.bank.app.application.command.PaymentResult;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanPaymentService {

    public PaymentResult processPayment(Loan loan, BigDecimal amount) {
        validateLoanForPayment(loan);

        LocalDate paymentDate = LocalDate.now();
        List<LoanInstallment> eligibleInstallments = getEligibleInstallments(loan, paymentDate);

        validatePaymentAmount(eligibleInstallments.get(0), amount);
        return processInstallmentPayments(eligibleInstallments, amount, paymentDate, loan);
    }

    private PaymentResult processInstallmentPayments(List<LoanInstallment> eligibleInstallments,
                                                     BigDecimal amount,
                                                     LocalDate paymentDate,
                                                     Loan loan) {
        BigDecimal remainingAmount = amount;
        BigDecimal totalPaid = BigDecimal.ZERO;
        int installmentsPaid = 0;

        for (LoanInstallment installment : eligibleInstallments) {
            BigDecimal requiredAmount = installment.calculatePaymentAmount(paymentDate);

            if (remainingAmount.compareTo(requiredAmount) >= 0) {
                installment.processPayment(requiredAmount, requiredAmount);
                remainingAmount = remainingAmount.subtract(requiredAmount);
                totalPaid = totalPaid.add(requiredAmount);
                installmentsPaid++;
            } else {
                break;
            }
        }

        loan.checkAndUpdatePaidStatus();
        return new PaymentResult(totalPaid, installmentsPaid);
    }

    private void validatePaymentAmount(LoanInstallment firstInstallment, BigDecimal amount) {
        BigDecimal requiredAmount = firstInstallment.getAmount();
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
