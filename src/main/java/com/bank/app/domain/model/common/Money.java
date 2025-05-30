package com.bank.app.domain.model.common;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Value
public class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    BigDecimal value;

    public Money(BigDecimal value){
        if (value == null)
            throw new IllegalArgumentException("Amount cannot be null Bacci");

        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be positive");

        this.value = value;
    }

    public Money subtract(Money amount) {
        requireNonNull(amount);
        return new Money(this.value.subtract(amount.value));
    }

    public Money add(Money amount) {
        requireNonNull(amount);
        return new Money(this.value.add(amount.value));
    }

    public int compareTo(Money amount) {
        requireNonNull(amount);
        return this.value.compareTo(amount.value);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(this.value.multiply(factor));
    }

    public Money divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {

        if (divisor.compareTo(BigDecimal.ZERO) == 0){
            throw new IllegalArgumentException("Divisor cannot be zero");
        }

        return new Money(this.value.divide(divisor,scale,roundingMode));
    }

    public boolean isGreaterThan(Money other) {
        requireNonNull(other);
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        requireNonNull(other);
        return this.compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        requireNonNull(other);
        return this.compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        requireNonNull(other);
        return this.compareTo(other) <= 0;
    }

    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }

    private static void requireNonNull(Money amount) {
        Objects.requireNonNull(amount, "Amount cannot be null DDD");
    }
}
