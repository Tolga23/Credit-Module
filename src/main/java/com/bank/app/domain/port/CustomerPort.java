package com.bank.app.domain.port;

import com.bank.app.domain.model.customer.Customer;

import java.util.Optional;

public interface CustomerPort {
    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    void update(Customer customer);
}
