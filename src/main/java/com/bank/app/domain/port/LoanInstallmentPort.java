package com.bank.app.domain.port;

import com.bank.app.domain.model.loan.LoanInstallment;

import java.util.List;
import java.util.Optional;

public interface LoanInstallmentPort {
    LoanInstallment save(LoanInstallment installment);
    Optional<LoanInstallment> findById(Long id);
    List<LoanInstallment> findByLoanId(Long loanId);
    void update(LoanInstallment installment);
}
