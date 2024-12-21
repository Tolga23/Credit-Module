package com.bank.app.domain.model.customer;

import com.bank.app.domain.exception.InsufficientCreditException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long id;
    private String name;
    private String surname;
    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit;

    public BigDecimal getAvailableCreditLimit() {
        return creditLimit.subtract(usedCreditLimit);
    }

    public boolean checkCreditLimit(BigDecimal creditLimit) {
        return getAvailableCreditLimit().compareTo(creditLimit) >= 0;
    }

    public void useCredit(BigDecimal amount) {
        if (!checkCreditLimit(amount))
            throw new InsufficientCreditException(amount, getAvailableCreditLimit());

        usedCreditLimit = usedCreditLimit.add(amount);
    }
}