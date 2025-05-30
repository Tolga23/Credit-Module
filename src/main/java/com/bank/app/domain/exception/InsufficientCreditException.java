package com.bank.app.domain.exception;

import com.bank.app.domain.model.common.Money;

public class InsufficientCreditException extends RuntimeException {
    public InsufficientCreditException(Money required, Money available) {
        super(String.format("Insufficient credit limit. Required: %s, Available: %s", required.getValue(), available.getValue()));
    }
}
