package com.bank.app.application.config;

import com.bank.app.domain.port.LoanCreatePort;
import com.bank.app.domain.port.LoanPaymentPort;
import com.bank.app.domain.service.LoanCreateService;
import com.bank.app.domain.service.LoanPaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public LoanCreatePort loanCreatePort() {
        return new LoanCreateService();
    }

    @Bean
    public LoanPaymentPort loanPaymentPort() {
        return new LoanPaymentService();
    }
}
