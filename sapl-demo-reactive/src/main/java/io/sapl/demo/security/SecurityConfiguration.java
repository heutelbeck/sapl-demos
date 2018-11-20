package io.sapl.demo.security;

import io.sapl.api.pdp.advice.AdviceHandlerService;
import io.sapl.api.pdp.advice.SimpleAdviceHandlerService;
import io.sapl.api.pdp.mapping.SaplMapper;
import io.sapl.api.pdp.mapping.SimpleSaplMapper;
import io.sapl.api.pdp.obligation.ObligationHandlerService;
import io.sapl.api.pdp.obligation.SimpleObligationHandlerService;
import io.sapl.demo.shared.advicehandlers.SimpleLoggingAdviceHandler;
import io.sapl.demo.shared.marshalling.AuthenticationMapper;
import io.sapl.demo.shared.marshalling.PatientMapper;
import io.sapl.demo.shared.obligationhandlers.EmailObligationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public SaplMapper saplMapper() {
        final SaplMapper saplMapper = new SimpleSaplMapper();
        saplMapper.register(new AuthenticationMapper());
        saplMapper.register(new PatientMapper());
        return saplMapper;
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
