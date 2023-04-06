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
package io.sapl.demo.web;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import io.sapl.spring.manager.SaplAuthorizationManager;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final SaplAuthorizationManager saplAuthzManager;

	@Bean
	public UserDetailsService  userDetailsService() {
		@SuppressWarnings("deprecation") // Demo Code!
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user")
				.password("user")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		return http.authorizeHttpRequests(requests -> 
						requests.anyRequest()
				                .access(saplAuthzManager)
				    )
				   .formLogin(withDefaults())
				   .httpBasic(withDefaults())
				   .build();
		// @formatter:on
	}

}
