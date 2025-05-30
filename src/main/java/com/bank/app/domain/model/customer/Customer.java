package com.bank.app.domain.model.customer;

import com.bank.app.domain.exception.InsufficientCreditException;
import com.bank.app.domain.model.common.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long id;
    private String name;
    private String surname;
    @Builder.Default
    private Money creditLimit = Money.ZERO;
    @Builder.Default
    private Money usedCreditLimit = Money.ZERO;

    public Money getAvailableCreditLimit() {
        return creditLimit.subtract(usedCreditLimit);
    }

    public boolean hasSufficientCredit(Money creditLimit) {
        return getAvailableCreditLimit().isGreaterThanOrEqual(creditLimit);
    }

    public void useCredit(Money amount) {
        if (!hasSufficientCredit(amount))
            throw new InsufficientCreditException(amount, getAvailableCreditLimit());

        usedCreditLimit = usedCreditLimit.add(amount);
    }

    public void releaseCredit(Money amount) {
        if (usedCreditLimit.isLessThan(amount))
            throw new IllegalArgumentException("Cannot release more credit than used");

        usedCreditLimit = usedCreditLimit.subtract(amount);
    }
}