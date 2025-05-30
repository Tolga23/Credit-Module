package com.bank.app.domain.model.common;

import lombok.Value;

import java.util.Set;

@Value
public class InstallmentCount {

    public static final Set<Integer> VALID_INSTALLMENTS = Set.of(6, 9, 12, 24);

    Integer numberOfInstallment;

    public InstallmentCount(Integer numberOfInstallment){
        if(numberOfInstallment == null)
            throw new IllegalArgumentException("Number of installments cannot be null");

        if (!VALID_INSTALLMENTS.contains(numberOfInstallment))
            throw new IllegalArgumentException("Number of installments must be one of: " + VALID_INSTALLMENTS);

        this.numberOfInstallment = numberOfInstallment;
    }
}
