package com.bank.app.infrastructure.config;

import com.bank.app.domain.model.user.User;
import com.bank.app.domain.model.user.UserRole;
import com.bank.app.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize users
        createUser("admin", "admin", UserRole.ROLE_ADMIN);
        createUser("customer", "customer", UserRole.ROLE_CUSTOMER);
    }

    private void createUser(String username, String password, UserRole userRole) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userRole);
        userRepository.save(user);
    }
}
