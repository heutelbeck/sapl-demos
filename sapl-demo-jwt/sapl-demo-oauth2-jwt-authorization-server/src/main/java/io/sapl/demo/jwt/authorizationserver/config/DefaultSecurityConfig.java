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
package io.sapl.demo.jwt.authorizationserver.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation") // NoOp Encoder OK for demo !
public class DefaultSecurityConfig {

    // @formatter:off
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        PathPatternRequestMatcher.Builder match = PathPatternRequestMatcher.withDefaults();
		http.authorizeHttpRequests (authorize -> authorize.requestMatchers(match.matcher("/public-key/**")).permitAll()
				        .anyRequest().authenticated()
		    )
		    .formLogin(withDefaults());
		return http.build();
	}
	// @formatter:on

    // @formatter:off
	@Bean
	UserDetailsService users() {
		var userDetailsService = new InMemoryUserDetailsManager();
		var user = User
				.withUsername("user1")
				.password("password")
				.roles("USER")
				.build();
		userDetailsService.createUser(user);
		return userDetailsService;
	}
	// @formatter:on

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
