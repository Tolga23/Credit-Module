package com.bank.app.domain.port;

import com.bank.app.domain.model.common.InstallmentCount;
import com.bank.app.domain.model.common.InterestRate;
import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;

import java.util.List;

public interface LoanCreatePort {
    Loan createLoan(Customer customer, Money loanAmount, InstallmentCount numberOfInstallments, InterestRate interestRate);
    List<LoanInstallment> createInstallment(Loan loan);
}
