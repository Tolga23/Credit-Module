package com.bank.app.application.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.LoanSearchCommand;
import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.command.PaymentResult;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.*;
import com.bank.app.infrastructure.adapters.in.rest.dto.response.PayLoanResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static final BigDecimal LOAN_AMOUNT = BigDecimal.valueOf(1000);
    private static final int INSTALLMENTS = 12;
    private static final BigDecimal INTEREST_RATE = BigDecimal.valueOf(0.1);

    private Customer mockCustomer;
    private Loan mockLoan;
    private List<LoanInstallment> mockInstallments;
    private PayLoanCommand payLoanCommand;

    @BeforeEach
    void setUp() {
        payLoanCommand = new PayLoanCommand(1L, 1L, BigDecimal.valueOf(100));
        // Set up reusable mock values
        mockCustomer = Customer.builder()
                .id(CUSTOMER_ID)
                .name("Test")
                .surname("Customer")
                .creditLimit(BigDecimal.valueOf(5000))
                .usedCreditLimit(BigDecimal.valueOf(1000))
                .build();

        mockLoan = Loan.builder()
                .id(1L)
                .customerId(CUSTOMER_ID)
                .loanAmount(LOAN_AMOUNT)
                .numberOfInstallment(INSTALLMENTS)
                .interestRate(INTEREST_RATE)
                .isPaid(false)
                .createdDate(LocalDateTime.now())
                .build();
        BigDecimal totalLoanAmount = LOAN_AMOUNT.multiply(BigDecimal.ONE.add(INTEREST_RATE));
        BigDecimal installmentAmount = totalLoanAmount.divide(BigDecimal.valueOf(INSTALLMENTS), 2, RoundingMode.HALF_UP);
        ;
        mockInstallments = Arrays.asList(
                LoanInstallment.builder()
                        .id(1L)
                        .loanId(1L)
                        .amount(installmentAmount)
                        .dueDate(LocalDate.now().plusMonths(1))
                        .isPaid(false)
                        .build(),
                LoanInstallment.builder()
                        .id(2L)
                        .loanId(1L)
                        .amount(installmentAmount)
                        .dueDate(LocalDate.now().plusMonths(2))
                        .isPaid(false)
                        .build()
        );
    }

    private CreateLoanCommand createLoanCommand() {
        return new CreateLoanCommand(CUSTOMER_ID, LOAN_AMOUNT, INSTALLMENTS, INTEREST_RATE);
    }

    @Test
    public void testCreateLoan_Success() {
        CreateLoanCommand command = createLoanCommand();

        // Arrange
        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));
        when(loanCreatePort.createLoan(mockCustomer, command)).thenReturn(mockLoan);
        when(loanPort.save(mockLoan)).thenReturn(mockLoan);
        when(loanCreatePort.createInstallment(mockLoan)).thenReturn(mockInstallments);
        when(loanInstallmentPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanApplicationService.createLoan(command);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(LOAN_AMOUNT, result.getLoanAmount());
        Assertions.assertEquals(INSTALLMENTS, result.getNumberOfInstallment());

        // Verify interactions
        verify(customerPort).findById(CUSTOMER_ID);
        verify(loanCreatePort).createLoan(mockCustomer, command);
        verify(customerPort).update(mockCustomer);
        verify(loanPort).save(mockLoan);
    }

    @Test
    public void testCreateLoan_CustomerNotFound() {
        CreateLoanCommand command = createLoanCommand();

        // Arrange
        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loanApplicationService.createLoan(command));

        // Verify
        verify(customerPort).findById(CUSTOMER_ID);
        verifyNoInteractions(loanPort, loanCreatePort);
    }

    @Test
    public void testPayLoan_Success() {
        PayLoanCommand command = new PayLoanCommand(CUSTOMER_ID, 1L, BigDecimal.valueOf(500));
        PaymentResult paymentResult = new PaymentResult(BigDecimal.valueOf(500), BigDecimal.valueOf(500), 1);

        // Arrange
        when(loanPort.findById(1L)).thenReturn(Optional.of(mockLoan));
        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));
        when(loanPaymentPort.processPayment(mockLoan, command)).thenReturn(paymentResult);

        // Act
        PayLoanResponse response = loanApplicationService.payLoan(command);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(paymentResult.totalPaid(), response.totalPaid());
        Assertions.assertEquals(paymentResult.installmentsPaid(), response.installmentsPaid());

        // Verify
        verify(loanPort).findById(1L);
        verify(loanPaymentPort).processPayment(mockLoan, command);
        verify(customerPort).update(mockCustomer);
        verify(loanPort).pay(mockLoan);
    }

    @Test
    public void testPayLoan_LoanNotFound() {
        PayLoanCommand command = new PayLoanCommand(CUSTOMER_ID, 1L, BigDecimal.valueOf(500));

        // Arrange
        when(loanPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loanApplicationService.payLoan(command));

        // Verify
        verify(loanPort).findById(1L);
        verifyNoInteractions(loanPaymentPort, customerPort);
    }

    @Test
    public void testGetCustomerLoans_Success() {
        LoanSearchCommand command = new LoanSearchCommand(CUSTOMER_ID, null, null);
        List<Loan> loans = Arrays.asList(mockLoan);

        // Arrange
        when(customerPort.findById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));
        when(loanPort.findByCustomerIdAndFilters(CUSTOMER_ID, null, null)).thenReturn(loans);

        // Act
        List<Loan> result = loanApplicationService.getCustomerLoans(command);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(mockLoan, result.get(0));

        // Verify
        verify(customerPort).findById(CUSTOMER_ID);
        verify(loanPort).findByCustomerIdAndFilters(CUSTOMER_ID, null, null);
    }

    @Test
    void testCreateLoan_InstallmentSaveFailed() {
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(mockCustomer));
        when(loanCreatePort.createLoan(any(), any())).thenReturn(mockLoan);
        when(loanPort.save(any())).thenReturn(mockLoan);
        when(loanCreatePort.createInstallment(any())).thenReturn(mockInstallments);
        when(loanInstallmentPort.save(any())).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> loanApplicationService.createLoan(createLoanCommand()));

        verify(loanInstallmentPort).save(any());
    }

    // Additional test for payLoan - validation failure
    @Test
    void testPayLoan_ValidationFailed() {
        when(loanPort.findById(anyLong())).thenReturn(Optional.of(mockLoan));
        when(loanPaymentPort.processPayment(any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid ownership"));

        assertThrows(IllegalArgumentException.class,
                () -> loanApplicationService.payLoan(payLoanCommand));

        verify(loanPaymentPort, times(1)).processPayment(any(), any());
    }

    // Additional test for getCustomerLoans - empty result
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
