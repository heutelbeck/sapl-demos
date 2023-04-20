package io.sapl.ethereum.demo.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class AuthenticatedUser {

	private final PrinterUserService    userRepository;
	private final AuthenticationContext authenticationContext;

	public AuthenticatedUser(AuthenticationContext authenticationContext, PrinterUserService userRepository) {
		this.userRepository        = userRepository;
		this.authenticationContext = authenticationContext;
	}

	public Optional<PrinterUser> get() {
		return authenticationContext.getAuthenticatedUser(UserDetails.class)
				.map(userDetails -> userRepository.loadUser(userDetails.getUsername()));
	}

	public void logout() {
		authenticationContext.logout();
	}

}
