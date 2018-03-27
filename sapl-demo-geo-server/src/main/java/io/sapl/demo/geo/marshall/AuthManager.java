package io.sapl.demo.geo.marshall;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.sapl.demo.geo.domain.CrewMember;
import io.sapl.demo.geo.domain.CrewRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthManager implements AuthenticationManager {

	private static final String NO_VALID_USER = "No valid user name provided.";
	private static final String USER_INACTIVE = "User is not active.";
	private static final String INVALID_PW = "User and/or password do not match.";
	private final BCryptPasswordEncoder passwdEncoder = new BCryptPasswordEncoder();
	private final CrewRepo crewRepo;

	@Override
	public Authentication authenticate(Authentication authentication) {
		String username = authentication.getPrincipal().toString();
		String rawPassword = authentication.getCredentials().toString();

		CrewMember flightAttendant = crewRepo.findById(username)
				.orElseThrow(() -> new BadCredentialsException(NO_VALID_USER));
		if (flightAttendant == null) {
			throw new BadCredentialsException(NO_VALID_USER);
		}
		if (!flightAttendant.isActive()) {
			throw new DisabledException(USER_INACTIVE);
		}

		if (!passwdEncoder.matches(rawPassword, flightAttendant.getPassword())) {
			throw new BadCredentialsException(INVALID_PW);
		}

		List<GrantedAuthority> userAuthorities = new ArrayList<>();
		userAuthorities.add(new SimpleGrantedAuthority(flightAttendant.getRole()));
		return new UsernamePasswordAuthenticationToken(username, flightAttendant.getPassword(), userAuthorities);
	}
}
