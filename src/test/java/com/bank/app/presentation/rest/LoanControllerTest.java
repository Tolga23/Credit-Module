package com.bank.app.presentation.rest;

import com.bank.app.config.SecurityTestConfig;
import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.service.LoanApplicationService;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.infrastructure.adapters.in.rest.LoanController;
import com.bank.app.infrastructure.adapters.in.rest.dto.request.CreateLoanRequest;
import com.bank.app.infrastructure.adapters.in.rest.dto.request.LoanSearchRequest;
import com.bank.app.infrastructure.adapters.in.rest.dto.request.PayLoanRequest;
import com.bank.app.infrastructure.adapters.in.rest.dto.response.PayLoanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
@Import(SecurityTestConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Default user for tests
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanApplicationService service;

    @Test
    public void testCreateLoan_AdminAccess() throws Exception {
        // Arrange
        CreateLoanRequest request = new CreateLoanRequest(1L, BigDecimal.valueOf(1000), 12, BigDecimal.valueOf(0.1));
        CreateLoanCommand command = CreateLoanCommand.from(request);
        Loan loan = Loan.builder()
                .id(1L)
                .customerId(1L)
                .loanAmount(BigDecimal.valueOf(1000))
                .numberOfInstallment(12)
                .interestRate(BigDecimal.valueOf(0.2))
                .build();

        Mockito.when(service.createLoan(command)).thenReturn(loan);

        // Act & Assert
        mockMvc.perform(post("/api/v1/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loan.getId()))
                .andExpect(jsonPath("$.loanAmount").value(loan.getLoanAmount()));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testCreateLoan_CustomerAccessDenied() throws Exception {
        // Arrange
        CreateLoanRequest request = new CreateLoanRequest(2L, BigDecimal.valueOf(2000), 12, BigDecimal.valueOf(0.15));

        // Act & Assert
        mockMvc.perform(post("/api/v1/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetCustomerLoans_AdminAccess() throws Exception {
        // Arrange
        LoanSearchRequest request = new LoanSearchRequest(1L, 12, true);
        Loan loan1 = Loan.builder()
                .id(1L)
                .customerId(1L)
                .loanAmount(BigDecimal.valueOf(1000))
                .numberOfInstallment(12)
                .createdDate(java.time.LocalDateTime.now())
                .isPaid(true)
                .build();
        Loan loan2 = Loan.builder()
                .id(2L)
                .customerId(2L)
                .loanAmount(BigDecimal.valueOf(2000))
                .numberOfInstallment(12)
                .createdDate(java.time.LocalDateTime.now())
                .isPaid(false)
                .build();

        Mockito.when(service.getCustomerLoans(Mockito.any())).thenReturn(Arrays.asList(loan1, loan2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/loan/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(loan1.getId()))
                .andExpect(jsonPath("$[1].id").value(loan2.getId()));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testPayLoan_CustomerAccessDenied() throws Exception {
        // Arrange
        PayLoanRequest request = new PayLoanRequest(1L, 1L, BigDecimal.valueOf(500));

        // Act & Assert
        mockMvc.perform(post("/api/v1/loan/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testPayLoan_AdminAccess() throws Exception {
        // Arrange
        PayLoanRequest request = new PayLoanRequest(1L, 1L, BigDecimal.valueOf(500));
        PayLoanCommand command = PayLoanCommand.from(request);

        PayLoanResponse response = new PayLoanResponse(
                BigDecimal.valueOf(500),
                2,
                false
        );

        Mockito.when(service.payLoan(command)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/loan/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPaid").value(500))
                .andExpect(jsonPath("$.installmentsPaid").value(2))
                .andExpect(jsonPath("$.loanFullyPaid").value(false));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testPayLoan_CustomerAccessDeniedForOtherCustomers() throws Exception {
        // Arrange
        PayLoanRequest request = new PayLoanRequest(2L, 1L, BigDecimal.valueOf(500));

        // Act & Assert
        mockMvc.perform(post("/api/v1/loan/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testPayLoan_InvalidPaymentAmount() throws Exception {
        // Arrange
        PayLoanRequest request = new PayLoanRequest(2L, 1L, BigDecimal.ZERO); // Invalid amount

        // Act & Assert
        mockMvc.perform(post("/api/v1/loan/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    // Helper method to convert objects to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
