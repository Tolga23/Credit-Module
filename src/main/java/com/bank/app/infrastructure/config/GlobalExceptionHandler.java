package com.bank.app.infrastructure.config;

import com.bank.app.domain.exception.InsufficientCreditException;
import com.bank.app.domain.exception.LoanException;
import com.bank.app.infrastructure.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientCreditException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientCreditException(InsufficientCreditException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
                ex.getMessage(),
                "INSUFFICIENT_CREDIT",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    

    @ExceptionHandler(LoanException.class)
    public ResponseEntity<ErrorResponse> handleLoanException(LoanException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
                ex.getMessage(),
                "LOAN_ERROR",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
                ex.getMessage(),
                "VALIDATION_ERROR",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
                ex.getMessage(),
                "BUSINESS_ERROR",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
                "An unexpected error occurred",
                "INTERNAL_ERROR",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
