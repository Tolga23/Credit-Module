package com.bank.app.domain.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.domain.exception.InsufficientCreditException;
import com.bank.app.domain.exception.UnauthorizedCustomerException;
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

    public Loan createLoan(Customer customer, CreateLoanCommand request) {
        Loan loan = Loan.createNewLoan(
                request.customerId(),
                request.amount(),
                request.numberOfInstallments(),
                request.interestRate());
        
        //  total loan amount = loanAmount * (1 + interestRate)
        validateAndUpdateCustomerCredit(customer, loan.getTotalLoanAmount());
        return loan;
    }

    public void validateAndUpdateCustomerCredit(Customer customer, BigDecimal loanAmount) {
        if (!customer.checkCreditLimit(loanAmount))
            throw new InsufficientCreditException(loanAmount, customer.getAvailableCreditLimit());

        customer.useCredit(loanAmount);
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

    public List<LoanInstallment> createInstallment(Loan loan) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal installmentAmount = loan.getInstallmentAmount();
        LocalDate firstDueDate = calculateFirstDueDate();

        for (int i = 1; i <= loan.getNumberOfInstallment(); i++) {
            LoanInstallment installment = LoanInstallment.builder()
                    .loanId(loan.getId())
                    .amount(installmentAmount)
                    .dueDate(firstDueDate.plusMonths(i))
                    .build();

            installment.validateInstallment();
            installments.add(installment);
        }

        return installments;
    }

    private LocalDate calculateFirstDueDate() {
        LocalDate now = LocalDate.now();
        return now.plusMonths(1).withDayOfMonth(1);
    }
}
