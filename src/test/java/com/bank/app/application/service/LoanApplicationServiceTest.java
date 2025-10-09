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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationServiceTest {

    @Mock
    private LoanPort loanPort;

    @Mock
    private CustomerPort customerPort;

    @Mock
    private LoanCreatePort loanCreatePort;

    @Mock
    private LoanInstallmentPort loanInstallmentPort;

    @Mock
    private LoanPaymentPort loanPaymentPort;

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    private static final Long CUSTOMER_ID = 1L;
    private static final Money LOAN_AMOUNT = new Money(BigDecimal.valueOf(1000));
    private static final int INSTALLMENTS = 12;
    private static final BigDecimal INTEREST_RATE_VALUE = BigDecimal.valueOf(0.12);

    private Customer mockCustomer;
    private Loan mockLoan;
    private List<LoanInstallment> mockInstallments;
    private PayLoanCommand payLoanCommand;

    @BeforeEach
    void setUp() {
        payLoanCommand = new PayLoanCommand(1L, 1L, BigDecimal.valueOf(100));

        mockCustomer = Customer.builder()
                .id(CUSTOMER_ID)
                .name("Test")
                .surname("Customer")
                .creditLimit(new Money(BigDecimal.valueOf(5000)))
                .usedCreditLimit(new Money(BigDecimal.valueOf(1000)))
                .build();

        InstallmentCount installmentCount = new InstallmentCount(INSTALLMENTS);
        InterestRate interestRate = new InterestRate(INTEREST_RATE_VALUE);

        mockLoan = Loan.builder()
                .id(1L)
                .customerId(CUSTOMER_ID)
                .loanAmount(LOAN_AMOUNT)
                .numberOfInstallment(installmentCount)
                .interestRate(interestRate)
                .isPaid(false)
                .createdDate(LocalDateTime.now())
                .build();

        Money totalLoanAmount = LOAN_AMOUNT.multiply(BigDecimal.ONE.add(interestRate.getInterestRate()));
        Money installmentAmount = totalLoanAmount.divide(BigDecimal.valueOf(installmentCount.getNumberOfInstallment()));
        mockInstallments = Arrays.asList(
                LoanInstallment.builder()
                        .id(1L)
                        .loanId(1L)
                        .amount(installmentAmount)
                        .paidAmount(Money.ZERO)
                        .dueDate(LocalDate.now().plusMonths(1))
                        .isPaid(false)
                        .build(),
                LoanInstallment.builder()
                        .id(2L)
                        .loanId(1L)
                        .amount(installmentAmount)
                        .paidAmount(Money.ZERO)
                        .dueDate(LocalDate.now().plusMonths(2))
                        .isPaid(false)
                        .build()
        );
    }

    private CreateLoanCommand createLoanCommand() {
        return new CreateLoanCommand(CUSTOMER_ID, LOAN_AMOUNT.getValue(), INSTALLMENTS, INTEREST_RATE_VALUE);
    }

    @Test
    public void testCreateLoan_Success() {
        CreateLoanCommand command = createLoanCommand();

        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));
        when(loanCreatePort.createLoan(eq(mockCustomer), any(Money.class), any(InstallmentCount.class), any(InterestRate.class)))
                .thenReturn(mockLoan);
        when(loanPort.save(mockLoan)).thenReturn(mockLoan);
        when(loanCreatePort.createInstallment(mockLoan)).thenReturn(mockInstallments);
        when(loanInstallmentPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanApplicationService.createLoan(command);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(LOAN_AMOUNT, result.getLoanAmount());
        Assertions.assertEquals(INSTALLMENTS, result.getNumberOfInstallment().getNumberOfInstallment());

        verify(customerPort).findById(CUSTOMER_ID);
        verify(loanCreatePort).createLoan(eq(mockCustomer), any(Money.class), any(InstallmentCount.class), any(InterestRate.class));
        verify(customerPort).update(mockCustomer);
        verify(loanPort).save(mockLoan);
        verify(loanInstallmentPort, times(mockInstallments.size())).save(any());
    }

    @Test
    public void testCreateLoan_CustomerNotFound() {
        CreateLoanCommand command = createLoanCommand();

        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> loanApplicationService.createLoan(command));

        verify(customerPort).findById(CUSTOMER_ID);
        verifyNoInteractions(loanPort, loanCreatePort);
    }

    @Test
    public void testPayLoan_Success() {
        PayLoanCommand command = new PayLoanCommand(CUSTOMER_ID, 1L, BigDecimal.valueOf(500));
        PaymentResult paymentResult = new PaymentResult(new Money(BigDecimal.valueOf(500)), new Money(BigDecimal.valueOf(500)), 1, true);

        when(loanPort.findById(1L)).thenReturn(Optional.of(mockLoan));
        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));
        when(loanPaymentPort.processPayment(eq(mockLoan), eq(CUSTOMER_ID), any(Money.class))).thenReturn(paymentResult);

        PaymentResult result = loanApplicationService.payLoan(command);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(paymentResult.totalPaid(), result.totalPaid());
        Assertions.assertEquals(paymentResult.installmentsPaid(), result.installmentsPaid());
        Assertions.assertEquals(paymentResult.loanFullyPaid(), result.loanFullyPaid());

        verify(loanPort).findById(1L);
        verify(loanPaymentPort).processPayment(eq(mockLoan), eq(CUSTOMER_ID), any(Money.class));
        verify(customerPort).update(mockCustomer);
        verify(loanPort).pay(mockLoan);
    }

    @Test
    public void testPayLoan_LoanNotFound() {
        PayLoanCommand command = new PayLoanCommand(CUSTOMER_ID, 1L, BigDecimal.valueOf(500));

        when(loanPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> loanApplicationService.payLoan(command));

        verify(loanPort).findById(1L);
        verifyNoInteractions(loanPaymentPort, customerPort);
    }

    @Test
    public void testGetCustomerLoans_Success() {
        LoanSearchCommand command = new LoanSearchCommand(CUSTOMER_ID, null, null);
        List<Loan> loans = Arrays.asList(mockLoan);

        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));
        when(loanPort.findByCustomerIdAndFilters(CUSTOMER_ID, null, null)).thenReturn(loans);

        List<Loan> result = loanApplicationService.getCustomerLoans(command);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(mockLoan, result.get(0));

        verify(customerPort).findById(CUSTOMER_ID);
        verify(loanPort).findByCustomerIdAndFilters(CUSTOMER_ID, null, null);
    }

    @Test
    void testCreateLoan_InstallmentSaveFailed() {
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(mockCustomer));
        when(loanCreatePort.createLoan(eq(mockCustomer), any(Money.class), any(InstallmentCount.class), any(InterestRate.class)))
                .thenReturn(mockLoan);
        when(loanPort.save(any())).thenReturn(mockLoan);
        when(loanCreatePort.createInstallment(any())).thenReturn(mockInstallments);
        when(loanInstallmentPort.save(any())).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.createLoan(createLoanCommand()));

        verify(loanInstallmentPort).save(any());
    }

    @Test
    void testPayLoan_ValidationFailed() {
        when(loanPort.findById(anyLong())).thenReturn(Optional.of(mockLoan));
        when(loanPaymentPort.processPayment(any(), anyLong(), any()))
                .thenThrow(new IllegalArgumentException("Invalid ownership"));

        assertThrows(IllegalArgumentException.class,
                () -> loanApplicationService.payLoan(payLoanCommand));

        verify(loanPaymentPort, times(1)).processPayment(any(), anyLong(), any());
    }

    @Test
    void testGetCustomerLoans_EmptyResult() {
        LoanSearchCommand searchCommand = new LoanSearchCommand(1L, 12, true);
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(mockCustomer));
        when(loanPort.findByCustomerIdAndFilters(anyLong(), anyInt(), anyBoolean()))
                .thenReturn(Collections.emptyList());

        List<Loan> results = loanApplicationService.getCustomerLoans(searchCommand);

        assertTrue(results.isEmpty());
        verify(customerPort).findById(searchCommand.customerId());
        verify(loanPort).findByCustomerIdAndFilters(
                searchCommand.customerId(),
                searchCommand.numberOfInstallments(),
                searchCommand.isPaid()
        );
    }
}





