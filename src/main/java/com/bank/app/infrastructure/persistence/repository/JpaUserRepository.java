package com.bank.app.infrastructure.persistence.repository;

import com.bank.app.domain.model.user.User;
import com.bank.app.domain.repository.UserRepository;
import com.bank.app.infrastructure.persistence.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;
    private final UserEntityMapper mapper;

    public JpaUserRepository(SpringDataUserRepository repository, UserEntityMapper mapper) {
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
