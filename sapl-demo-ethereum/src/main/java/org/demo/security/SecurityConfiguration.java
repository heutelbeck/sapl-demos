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
package org.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private static final String LOGIN_PROCESSING_URL = "/login";

	private static final String LOGIN_FAILURE_URL = "/login";

	private static final String LOGIN_URL = "/login";

	private static final String LOGOUT_SUCCESS_URL = "/login";

	/**
	 * Require login to access internal pages and configure login form.
	 */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		return http  .csrf()
				     .disable()
				     .requestCache()
				     .requestCache(new CustomRequestCache())
			   .and().authorizeHttpRequests()
		             .requestMatchers(SecurityUtils::isFrameworkInternalRequest)
		             .permitAll()
		             .anyRequest()
		             .authenticated()
		       .and().formLogin()
		             .loginPage(LOGIN_URL)
		             .permitAll()
		             .loginProcessingUrl(LOGIN_PROCESSING_URL)
		             .failureUrl(LOGIN_FAILURE_URL)
		       .and().logout()
		             .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
		             .invalidateHttpSession(true)
		       .and().build();
		// @formatter:on
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// @formatter:off
		return web -> web.ignoring()
				         .requestMatchers("/VAADIN/**", 
				      	    	          "/favicon.ico", 
				        	    	      "/robots.txt", 
				        	    	      "/manifest.webmanifest", 
				        	    	      "/sw.js",
				        	    	      "/offline-page.html", 
				        	    	      "/icons/**", 
				        	    	      "/images/**", 
				        	    	      "/frontend/**", 
				        	    	      "/webjars/**", 
				        	    	      "/h2-console/**",
				        	    	      "/frontend-es5/**", 
				        		          "/frontend-es6/**");
		// @formatter:on
	}

}
