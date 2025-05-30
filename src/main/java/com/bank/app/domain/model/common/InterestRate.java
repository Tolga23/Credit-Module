package com.bank.app.domain.model.common;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Value
public class InterestRate {

    public static final BigDecimal MIN_INTEREST_RATE = new BigDecimal("0.1").setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal MAX_INTEREST_RATE = new BigDecimal("0.5").setScale(2, RoundingMode.HALF_UP);

    BigDecimal interestRate;

    public InterestRate(BigDecimal interestRate){

        if(interestRate == null){
            throw new IllegalArgumentException("Interest rate cannot be null");
        }

        BigDecimal scaledValue = interestRate.setScale(2, RoundingMode.HALF_UP);

        if(scaledValue.compareTo(MIN_INTEREST_RATE) < 0 || scaledValue.compareTo(MAX_INTEREST_RATE) > 0){
            throw new IllegalArgumentException("Interest rate must be between  " + MIN_INTEREST_RATE + " and " + MAX_INTEREST_RATE);
        }

      this.interestRate = scaledValue;
    }
}
