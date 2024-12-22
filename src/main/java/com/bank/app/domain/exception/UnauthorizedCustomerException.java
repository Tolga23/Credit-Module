package com.bank.app.domain.exception;

public class UnauthorizedCustomerException extends RuntimeException {
    public UnauthorizedCustomerException(String message) {
        super(message);
    }
}
