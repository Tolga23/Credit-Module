package com.bank.app.domain.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.domain.model.common.InterestRate;
import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanCreatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanCreateService implements LoanCreatePort {

    @Override
    public Loan createLoan(Customer customer, CreateLoanCommand request) {
        Money loanAmount = new Money(request.amount());
        InterestRate interestRate = new InterestRate(request.interestRate());

        Loan loan = Loan.createNewLoan(
                request.customerId(),
                loanAmount,
                request.numberOfInstallments(),
                interestRate
                );

        //  total loan amount = loanAmount * (1 + interestRate)
        Money totalLoanAmount = loan.getTotalLoanAmount();
        customer.useCredit(totalLoanAmount);
        return loan;
    }

    @Override
    public List<LoanInstallment> createInstallment(Loan loan) {
        List<LoanInstallment> installments = new ArrayList<>();
        Money installmentAmount = loan.getInstallmentAmount();
        LocalDate firstDueDate = calculateFirstDueDate();

        for (int i = 1; i <= loan.getNumberOfInstallment(); i++) {
            LoanInstallment installment = LoanInstallment.builder()
                    .loanId(loan.getId())
                    .amount(installmentAmount)
                    .paidAmount(Money.ZERO)
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
