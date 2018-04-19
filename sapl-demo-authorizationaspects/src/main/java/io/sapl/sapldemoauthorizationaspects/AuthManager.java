package io.sapl.sapldemoauthorizationaspects;

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

import io.sapl.demo.domain.User;
import io.sapl.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthManager implements AuthenticationManager {

	private final BCryptPasswordEncoder passwdEncoder = new BCryptPasswordEncoder();

	private final UserRepo userRepo;

	@Override
	public Authentication authenticate(Authentication authentication) {
		String username = authentication.getPrincipal().toString();

		User user = userRepo.findById(username)
				.orElseThrow(() -> new BadCredentialsException("no valid user name provided"));
		if (user == null) {
			throw new BadCredentialsException("no user name provided");
		}
		if (user.isDisabled()) {
			throw new DisabledException("user disabled");
		}
		String rawPassword = authentication.getCredentials().toString();

		if (!passwdEncoder.matches(rawPassword, user.getPassword())) {
			throw new BadCredentialsException("user and/or password do not match");
		}
		List<GrantedAuthority> userAuthorities = new ArrayList<>();
		user.getFunctions().forEach(function -> userAuthorities.add(new SimpleGrantedAuthority(function)));
		return new UsernamePasswordAuthenticationToken(username, user.getPassword(), userAuthorities);

	}

}
