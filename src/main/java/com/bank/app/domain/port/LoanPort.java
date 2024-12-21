package com.bank.app.domain.port;

import com.bank.app.domain.model.loan.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanPort {
    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    List<Loan> findByCustomerId(Long customerId);

    void update(Loan loan);
}