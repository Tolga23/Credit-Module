package com.bank.app.presentation.dto.response;

import java.math.BigDecimal;

public record PayLoanResponse(
        BigDecimal totalPaid,
        int installmentsPaid,
        boolean loanFullyPaid
) {}