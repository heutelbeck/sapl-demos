package io.sapl.demo.axon.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

	@Bean
	SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        // @formatter:off
        http.cors(withDefaults()).csrf(CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .formLogin(withDefaults()); 
		return http.build();
		// @formatter:on
	}
}
