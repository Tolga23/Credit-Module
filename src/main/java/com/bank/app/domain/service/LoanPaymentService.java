package com.bank.app.domain.service;

import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.command.PaymentResult;
import com.bank.app.domain.exception.UnauthorizedCustomerException;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanPaymentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanPaymentService implements LoanPaymentPort {

    @Override
    public PaymentResult processPayment(Loan loan, PayLoanCommand request) {
        validateLoanForPayment(loan);
        validateOwnership(loan, request.customerId());

        LocalDate paymentDate = LocalDate.now();
        List<LoanInstallment> eligibleInstallments = getEligibleInstallments(loan, paymentDate);

        validatePaymentAmount(eligibleInstallments.get(0), request.amount());
        return processInstallmentPayments(eligibleInstallments, request.amount(), paymentDate, loan);
    }

    private PaymentResult processInstallmentPayments(List<LoanInstallment> eligibleInstallments,
                                                     BigDecimal amount,
                                                     LocalDate paymentDate,
                                                     Loan loan) {
        BigDecimal remainingAmount = amount;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal originalAmount = BigDecimal.ZERO;
        int installmentsPaid = 0;

        for (LoanInstallment installment : eligibleInstallments) {
            BigDecimal requiredAmount = installment.calculatePaymentAmount(paymentDate);

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
        return new PaymentResult(totalPaid, originalAmount, installmentsPaid);
    }

    public void validateOwnership(Loan loan, Long customerId) {
        // Check ownership of loan
        if (!loan.getCustomerId().equals(customerId)) {
            throw new UnauthorizedCustomerException("You do not have permission to access this loan");
        }

        // Check if loan is already paid
        if (loan.isPaid()) {
            throw new IllegalStateException("Loan is already paid.");
        }
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
