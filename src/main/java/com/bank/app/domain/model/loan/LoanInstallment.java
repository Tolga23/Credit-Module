package com.bank.app.domain.model.loan;

import com.bank.app.domain.model.common.Money;
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
    private Money amount;
    private Money paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    @Builder.Default
    private boolean isPaid = false;

    private static final BigDecimal DAILY_RATE = BigDecimal.valueOf(0.001);

    public void validateInstallment() {
        if (loanId == null)
            throw new IllegalArgumentException("Load ID is required.");



        if (dueDate == null || dueDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("DueDate cannot be in the past or null.");
    }

    public Money calculatePaymentAmount(LocalDate paymentDate) {
        long daysDifference = ChronoUnit.DAYS.between(paymentDate, dueDate);

        if (daysDifference > 0) {
            // Early payment discount
            BigDecimal discountRate = DAILY_RATE.multiply(BigDecimal.valueOf(daysDifference));
            Money discount = amount.multiply(discountRate);
            return amount.subtract(discount);
        } else if (daysDifference < 0) {
            // Late payment penalty
            BigDecimal penaltyRate = DAILY_RATE.multiply(BigDecimal.valueOf(Math.abs(daysDifference)));
            Money penalty = amount.multiply(penaltyRate);
            return amount.add(penalty);
        }
        return amount;
    }

    public void processPayment(Money paymentAmount, Money requiredAmount) {
        if (isPaid) {
            throw new IllegalStateException("Installment is already paid");
        }

        if (paymentAmount.compareTo(requiredAmount) >= 0) {
            this.paidAmount = requiredAmount;
            this.paymentDate = LocalDate.now();
            this.isPaid = true;
        } else {
            throw new IllegalArgumentException("Payment amount must be at least " + requiredAmount);
        }
    }
}
