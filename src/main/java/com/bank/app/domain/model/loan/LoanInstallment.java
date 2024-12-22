package com.bank.app.domain.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    private static final BigDecimal DAILY_RATE = BigDecimal.valueOf(0.001);

    public void validateInstallment() {
        if (loanId == null)
            throw new IllegalArgumentException("Load ID is required.");

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be positive");

        if (dueDate == null || dueDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("DueDate cannot be in the past or null.");
    }

    public BigDecimal calculatePaymentAmount(LocalDate paymentDate) {
        long daysDifference = ChronoUnit.DAYS.between(paymentDate, dueDate);

        if (daysDifference > 0) {
            // Early payment discount
            BigDecimal discountRate = DAILY_RATE.multiply(BigDecimal.valueOf(daysDifference));
            BigDecimal discount = amount.multiply(discountRate);
            return amount.subtract(discount);
        } else if (daysDifference < 0) {
            // Late payment penalty
            BigDecimal penaltyRate = DAILY_RATE.multiply(BigDecimal.valueOf(Math.abs(daysDifference)));
            BigDecimal penalty = amount.multiply(penaltyRate);
            return amount.add(penalty);
        }
        return amount;
    }

    public void processPayment(BigDecimal paymentAmount, LocalDate paymentDate) {
        if (isPaid) {
            throw new IllegalStateException("Installment is already paid");
        }

        BigDecimal requiredAmount = calculatePaymentAmount(paymentDate);
        if (paymentAmount.compareTo(requiredAmount) >= 0) {
            this.paidAmount = requiredAmount;
            this.paymentDate = paymentDate;
            this.isPaid = true;
        } else {
            throw new IllegalArgumentException("Payment amount must be at least " + requiredAmount);
        }
    }
}
