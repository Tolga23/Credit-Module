package com.bank.app.infrastructure.adapters.in.rest.dto.response;

import java.math.BigDecimal;

public record PayLoanResponse(
        BigDecimal totalPaid,
        int installmentsPaid,
        boolean loanFullyPaid
) {}