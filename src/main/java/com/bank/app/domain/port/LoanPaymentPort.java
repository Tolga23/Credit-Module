package com.bank.app.domain.port;

import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.command.PaymentResult;
import com.bank.app.domain.model.loan.Loan;

public interface LoanPaymentPort {
    PaymentResult processPayment(Loan loan, PayLoanCommand request);
}

