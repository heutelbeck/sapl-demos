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
package io.sapl.demo.books.security;

import io.sapl.demo.books.data.DemoData;
import io.sapl.demo.books.domain.LibraryUserDetails;
import io.sapl.spring.config.EnableSaplMethodSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableSaplMethodSecurity
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutUrl("/logout"))
                .build();
    }

    /**
     * Custom UserDetailsService that preserves our LibraryUserDetails type.
     * <p>
     * We intentionally do NOT use InMemoryUserDetailsManager because it implements
     * UserDetailsPasswordService, which causes password upgrade logic to
     * convert our custom UserDetails into a generic User object.
     */
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        Map<String, UserDetails> users = new ConcurrentHashMap<>();
        Stream.of(DemoData.users(passwordEncoder))
                .forEach(user -> users.put(user.getUsername().toLowerCase(), user));
        return username -> {
            var user = users.get(username.toLowerCase());
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            return user;
        };
    }

    /**
     * Custom authentication provider that extracts the clean LibraryUser as the principal.
     * <p>
     * After successful credential validation, the Authentication's principal is set to
     * the LibraryUser (identity object) rather than the LibraryUserDetails (auth wrapper).
     * This ensures no credentials leak into the authorization context.
     */
    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        var delegate = new DaoAuthenticationProvider(userDetailsService);
        delegate.setPasswordEncoder(passwordEncoder);

        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                var auth = delegate.authenticate(authentication);
                if (auth.getPrincipal() instanceof LibraryUserDetails details) {
                    // Replace principal with the clean identity object
                    return new UsernamePasswordAuthenticationToken(
                            details.libraryUser(),  // Clean principal - no password!
                            null,                   // No credentials in the result
                            details.getAuthorities()
                    );
                }
                return auth;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return delegate.supports(authentication);
            }
        };
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
