package com.bank.app.domain.service;

import com.bank.app.domain.exception.InsufficientCreditException;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanDomainService {

    public void validateCustomerCredit(Customer customer, BigDecimal loanAmount) {
        if (!customer.checkCreditLimit(loanAmount))
            throw new InsufficientCreditException(loanAmount, customer.getAvailableCreditLimit());
    }

    public List<LoanInstallment> createInstallment(Loan loan) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal installmentAmount = loan.getInstallmentAmount();
        LocalDate firstDueDate = calculateFirstDueDate();

        for (int i = 0; i < loan.getNumberOfInstallment(); i++) {
            LoanInstallment installment = LoanInstallment.builder()
                    .loanId(loan.getId())
                    .amount(installmentAmount)
                    .dueDate(firstDueDate.plusMonths(1))
                    .build();

            installment.validateInstallment();
            installments.add(installment);
        }

        return installments;
    }

    private LocalDate calculateFirstDueDate() {
        return LocalDate.now()
                .plusMonths(1)
                .plusDays(1);
    }
}
