package org.demo.security;

import org.demo.shared.advicehandlers.SimpleLoggingAdviceHandler;
import org.demo.shared.obligationhandlers.EmailObligationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.sapl.api.pdp.advice.AdviceHandlerService;
import io.sapl.api.pdp.obligation.ObligationHandlerService;
import io.sapl.pep.pdp.advice.SimpleAdviceHandlerService;
import io.sapl.pep.pdp.obligation.SimpleObligationHandlerService;

@Configuration
public class SecurityConfiguration {

    static {
        SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObligationHandlerService obligationHandlerService() {
        final ObligationHandlerService ohs = new SimpleObligationHandlerService();
        ohs.register(new EmailObligationHandler());
        return ohs;
    }

    @Bean
    public AdviceHandlerService adviceHandlerService() {
        final AdviceHandlerService ahs = new SimpleAdviceHandlerService();
        ahs.register(new SimpleLoggingAdviceHandler());
        return ahs;
    }

}
