package com.bank.app.infrastructure.adapters.user.repository;

import com.bank.app.domain.model.user.User;
import com.bank.app.domain.port.UserPort;
import com.bank.app.infrastructure.adapters.user.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserPort implements UserPort {

    private final SpringDataUserRepository repository;
    private final UserEntityMapper mapper;

    public JpaUserPort(SpringDataUserRepository repository, UserEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // Created for test data initialize, logic doesn't added because of that.
    @Override
    public User save(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(this.mapper::toDomain);
    }
}
