package com.bank.app.domain.port;

import com.bank.app.domain.model.loan.PaymentResult;
import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.loan.Loan;

public interface LoanPaymentPort {
    PaymentResult processPayment(Loan loan, Long customerId, Money paymentAmount);
}


