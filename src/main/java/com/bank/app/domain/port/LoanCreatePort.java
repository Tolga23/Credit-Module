package com.bank.app.domain.port;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;

import java.util.List;

public interface LoanCreatePort {
    Loan createLoan(Customer customer, CreateLoanCommand request);
    List<LoanInstallment> createInstallment(Loan loan);
}
