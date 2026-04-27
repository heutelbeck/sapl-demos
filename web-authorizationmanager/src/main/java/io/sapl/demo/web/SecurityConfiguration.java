/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.demo.web;

import static io.sapl.spring.pep.http.servlet.SaplHttpSecurityConfigurer.saplHttp;
import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import io.sapl.api.pdp.AuthorizationSubscription;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    UserDetailsService userDetailsService() {
        @SuppressWarnings("deprecation") // Demo Code
        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build();
        return new InMemoryUserDetailsManager(user);
    }

    /*
     * The configurer customizer narrows the subscription to the three fields
     * the demo policies reference (subject..authority, action.method,
     * resource.requestedURI). The default factory ships the entire
     * serialized request which works but is verbose. To replace the factory
     * globally instead, declare a single
     * @Bean AuthorizationSubscriptionFactory and the configurer call below
     * collapses to http.with(saplHttp(), withDefaults()).
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, ObjectMapper mapper) throws Exception {
        return http.with(saplHttp(),
                c -> c.subscriptionFactory((auth, request) -> AuthorizationSubscription.of(auth,
                        Map.of("method", request.getMethod()),
                        Map.of("requestedURI", request.getRequestURI()), mapper)))
                .formLogin(withDefaults()).httpBasic(withDefaults()).build();
    }

}
