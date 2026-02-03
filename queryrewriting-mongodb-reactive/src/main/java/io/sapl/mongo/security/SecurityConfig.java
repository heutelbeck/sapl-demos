/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.mongo.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import io.sapl.mongo.data.DemoData;
import io.sapl.mongo.domain.LibraryUserDetails;
import io.sapl.spring.config.EnableReactiveSaplMethodSecurity;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableReactiveSaplMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
            ReactiveAuthenticationManager authenticationManager) {
        return http
                .authenticationManager(authenticationManager)
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutUrl("/logout"))
                .build();
    }

    /**
     * Custom ReactiveUserDetailsService that preserves our LibraryUserDetails type.
     * <p>
     * We intentionally do NOT use MapReactiveUserDetailsService because it implements
     * ReactiveUserDetailsPasswordService, which causes password upgrade logic to
     * convert our custom UserDetails into a generic User object.
     */
    @Bean
    ReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        Map<String, UserDetails> users = new ConcurrentHashMap<>();
        Stream.of(DemoData.users(passwordEncoder))
                .forEach(user -> users.put(user.getUsername().toLowerCase(), user));
        return username -> Mono.justOrEmpty(users.get(username.toLowerCase()));
    }

    /**
     * Custom authentication manager that extracts the clean LibraryUser as the principal.
     * <p>
     * After successful credential validation, the Authentication's principal is set to
     * the LibraryUser (identity object) rather than the LibraryUserDetails (auth wrapper).
     * This ensures no credentials leak into the authorization context.
     */
    @Bean
    ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        var delegate = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        delegate.setPasswordEncoder(passwordEncoder);

        // Wrap to transform the principal from LibraryUserDetails to LibraryUser
        return authentication -> delegate.authenticate(authentication)
                .map(auth -> {
                    if (auth.getPrincipal() instanceof LibraryUserDetails details) {
                        // Replace principal with the clean identity object
                        return new UsernamePasswordAuthenticationToken(
                                details.libraryUser(),  // Clean principal - no password!
                                null,                   // No credentials in the result
                                details.getAuthorities()
                        );
                    }
                    return auth;
                });
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
