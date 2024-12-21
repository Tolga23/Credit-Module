package com.bank.app.infrastructure.config;

import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.user.User;
import com.bank.app.domain.model.user.UserRole;
import com.bank.app.infrastructure.adapters.customer.CustomerDataAdapter;
import com.bank.app.infrastructure.adapters.user.UserDataAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserDataAdapter userDataAdapter;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDataAdapter customerDataAdapter;

    @Override
    public void run(String... args) throws Exception {
        // Initialize users
        createAdminUser();
        createCustomer();
    }

    private void createAdminUser() {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(UserRole.ROLE_ADMIN)
                .build();
        userDataAdapter.save(admin);
    }

    private void createCustomer() {
        Customer customer = Customer.builder()
                .name("Test")
                .surname("Customer")
                .creditLimit(BigDecimal.valueOf(10000))
                .usedCreditLimit(BigDecimal.ZERO)
                .build();
        Customer savedCustomer = customerDataAdapter.save(customer);

        User customerUser = User.builder()
                .username("customer")
                .password(passwordEncoder.encode("customer"))
                .role(UserRole.ROLE_CUSTOMER)
                .customerId(savedCustomer.getId())
                .build();
        userDataAdapter.save(customerUser);
    }
}
