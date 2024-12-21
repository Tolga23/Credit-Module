package com.bank.app.infrastructure.adapters.customer;

import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.port.CustomerPort;
import com.bank.app.infrastructure.adapters.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerDataAdapter implements CustomerPort {
    private final CustomerJpaRepository repository;

    @Override
    public Customer save(Customer customer) {
        return null;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void update(Customer customer) {

    }
}
