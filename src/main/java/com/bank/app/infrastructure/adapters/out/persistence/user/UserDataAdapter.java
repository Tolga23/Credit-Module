package com.bank.app.infrastructure.adapters.out.persistence.user;

import com.bank.app.domain.model.user.User;
import com.bank.app.domain.port.UserPort;
import com.bank.app.infrastructure.adapters.out.persistence.user.mapper.UserEntityMapper;
import com.bank.app.infrastructure.adapters.out.persistence.user.repository.UserJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDataAdapter implements UserPort {

    private final UserJpaRepository repository;
    private final UserEntityMapper mapper;

    public UserDataAdapter(UserJpaRepository repository, UserEntityMapper mapper) {
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
