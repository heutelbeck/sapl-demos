package io.sapl.geo.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sapl.geo.demo.domain.GeoUserRepository;
import io.sapl.geo.functionlibraries.GeoFunctions;
import io.sapl.server.*;
import io.sapl.spring.config.EnableSaplMethodSecurity;


@Configuration
@EnableWebSecurity
@EnableSaplMethodSecurity
public class SecurityConfiguration {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directive.ALL));
        
        return http
        		
        		.authorizeHttpRequests(requests -> requests
        				.requestMatchers("/kml/features").permitAll()
        				.anyRequest().authenticated()
        		)

                   .formLogin(login -> login.defaultSuccessUrl("/mainView", true))
                   .logout(logout -> logout.permitAll()
                		   .logoutUrl("/logout")
                           .logoutSuccessUrl("/login")
                           .addLogoutHandler(clearSiteData))
                   .csrf(csrf -> csrf.disable()) //use /app/move
                   .build();
        
    }

	@Bean
	GeoFunctions geoFunctions() {
		
		return new GeoFunctions();
	}
	
	@Bean
	TraccarPolicyInformationPoint traccarPolicyInformationPoint() {
		
		return new TraccarPolicyInformationPoint(mapper);
	}
	
	@Bean
	OwnTracksPolicyInformationPoint ownTracksPolicyInformationPoint() {
		
		return new OwnTracksPolicyInformationPoint(mapper);
	}
	
	@Bean
	PostGisPolicyInformationPoint postGisPolicyInformationPoint() {
		
		return new PostGisPolicyInformationPoint(mapper);
	}
	
    @Bean
    static PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    UserDetailsService userDetailsService(GeoUserRepository geoUserRepository) {
        return new GeoUserDetailsService(geoUserRepository);
    }
	
}
