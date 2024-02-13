/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.pdp.multitenenat.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import io.sapl.pdp.multitenant.MultiTenantConfiguration;
import io.sapl.pdp.multitenenat.demo.domain.DemoData;
import io.sapl.spring.config.EnableSaplMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableSaplMethodSecurity
@Import(MultiTenantConfiguration.class)
public class SecurityConfiguration {

	@Bean
	UserDetailsService userDetailsService() {
		var service = new TenantAwareUserDetailsService();
		DemoData.loadUsers(service, passwordEncoder());
		return service;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		return http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
				   .formLogin(login -> login.defaultSuccessUrl("/patients/1", true))
				   .logout(logout -> logout.permitAll().logoutSuccessUrl("/login"))
				   .csrf(CsrfConfigurer::disable)
                   .build();
		// @formatter:on
	}

}
