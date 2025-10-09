package com.bank.app.application.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.LoanSearchCommand;
import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.domain.model.loan.PaymentResult;
import com.bank.app.domain.model.common.InstallmentCount;
import com.bank.app.domain.model.common.InterestRate;
import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.CustomerPort;
import com.bank.app.domain.port.LoanCreatePort;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.domain.port.LoanPaymentPort;
import com.bank.app.domain.port.LoanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    private final LoanPort loanPort;
    private final CustomerPort customerPort;
    private final LoanCreatePort loanCreatePort;
    private final LoanInstallmentPort loanInstallmentPort;
    private final LoanPaymentPort loanPaymentPort;

    @Transactional
    public Loan createLoan(CreateLoanCommand command) {
        Customer customer = customerPort.findById(command.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Money loanAmount = new Money(command.amount());
        InterestRate interestRate = new InterestRate(command.interestRate());
        InstallmentCount numberOfInstallments = new InstallmentCount(command.numberOfInstallments());

        Loan loan = loanCreatePort.createLoan(customer, loanAmount, numberOfInstallments, interestRate);
        customerPort.update(customer);

        loan = loanPort.save(loan);
        if (loan == null) {
            throw new IllegalStateException("Loan couldn't be saved");
        }

        createAndSaveInstallments(loan);

        return loan;
    }

    @Transactional
    public PaymentResult payLoan(PayLoanCommand command) {
        Loan loan = loanPort.findById(command.loanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        Money paymentAmount = new Money(command.amount());
        PaymentResult result = loanPaymentPort.processPayment(loan, command.customerId(), paymentAmount);

        if (result.totalPaid().compareTo(Money.ZERO) > 0) {
            loan.getInstallments().stream()
                    .filter(LoanInstallment::isPaid)
                    .forEach(loanInstallmentPort::update);

            loanPort.pay(loan);

            updateCustomerCredit(command.customerId(), result.originalAmount());
        }

        return result;
    }

    private void updateCustomerCredit(Long customerId, Money originalAmount) {
        Customer customer = customerPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        customer.releaseCredit(originalAmount);
        customerPort.update(customer);
    }

    private void createAndSaveInstallments(Loan savedLoan) {
        List<LoanInstallment> installments = loanCreatePort.createInstallment(savedLoan);
        installments.forEach(installment -> {
            LoanInstallment savedInstallment = loanInstallmentPort.save(installment);
            if (savedInstallment == null) {
                throw new IllegalStateException("Failed to save loan installment");
            }
            savedLoan.addInstallments(savedInstallment);
        });
    }

    public List<Loan> getCustomerLoans(LoanSearchCommand command) {
        customerPort.findById(command.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return loanPort.findByCustomerIdAndFilters(
                command.customerId(),
                command.numberOfInstallments(),
                command.isPaid());
    }
}

