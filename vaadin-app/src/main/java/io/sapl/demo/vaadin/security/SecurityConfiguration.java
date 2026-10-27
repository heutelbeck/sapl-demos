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
package io.sapl.demo.vaadin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;

import io.sapl.demo.vaadin.views.LoginView;
import lombok.val;

/**
 * Form based login for the Vaadin views with two demo accounts. The SAPL
 * policies decide what the accounts may see and do, this configuration only
 * establishes who they are.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        val matcher = PathPatternRequestMatcher.withDefaults();
        http.authorizeHttpRequests(auth -> auth.requestMatchers(matcher.matcher("/images/*")).permitAll());
        http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> vaadin.loginView(LoginView.class));
        return http.build();
    }

    /**
     * Two accounts for the demo, credentials admin/admin and user/user.
     *
     * @return the in memory user store
     */
    @Bean
    UserDetailsService userDetailsService() {
        val admin = User.withUsername("admin")
                .password("{bcrypt}$2a$10$2U4/YBJvAHYN84sd4P2ie.rGs3bqcySy.HAP2zplSKrFbIBUj.a5u").roles("Admin")
                .build();
        val user  = User.withUsername("user")
                .password("{bcrypt}$2a$10$uQq021lkTnSppaRsv16LUOXtf4NkIOkh.xONE/FCDXkbHuAQ5Hnuu").roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

}
