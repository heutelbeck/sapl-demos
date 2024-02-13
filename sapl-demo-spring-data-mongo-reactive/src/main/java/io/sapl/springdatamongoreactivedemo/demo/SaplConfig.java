package io.sapl.springdatamongoreactivedemo.demo;

import io.sapl.api.pdp.AuthorizationSubscription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SaplConfig {

    @Bean
    AuthorizationSubscription generalProtectionProtectedReactiveMongoUserRepository() {
        return AuthorizationSubscription.of("Test from bean authsub", "general_protection", "");
    }

}
