package com.bank.app.infrastructure.adapters.customer;

import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.port.CustomerPort;
import com.bank.app.infrastructure.adapters.customer.entity.CustomerEntity;
import com.bank.app.infrastructure.adapters.customer.mapper.CustomerEntityMapper;
import com.bank.app.infrastructure.adapters.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerDataAdapter implements CustomerPort {
    private final CustomerJpaRepository repository;
    private final CustomerEntityMapper mapper;

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void update(Customer customer) {
        repository.save(mapper.toEntity(customer));
    }
}
