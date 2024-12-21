package com.bank.app.domain.exception;

import java.math.BigDecimal;

public class InsufficientCreditException extends RuntimeException {
    public InsufficientCreditException(BigDecimal required, BigDecimal available) {
        super(String.format("Insufficient credit limit. Required: %s, Available: %s", required, available));
    }
}
