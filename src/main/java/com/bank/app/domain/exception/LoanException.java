package com.bank.app.domain.exception;

public class LoanException extends RuntimeException {
    public LoanException(String message) {
        super(message);
    }
}
