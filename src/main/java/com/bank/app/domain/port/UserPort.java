package com.bank.app.domain.port;

import com.bank.app.domain.model.user.User;

import java.util.Optional;

public interface UserPort {
    User save(User user);
    Optional<User> findByUsername(String username);
}
