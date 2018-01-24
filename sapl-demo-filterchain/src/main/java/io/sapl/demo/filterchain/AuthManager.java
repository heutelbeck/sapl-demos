package io.sapl.demo.filterchain;


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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthManager implements AuthenticationManager{

	private final BCryptPasswordEncoder passwdEncoder = new BCryptPasswordEncoder();
	
	private final UserRepo userRepo;

	@Override
	public Authentication authenticate(Authentication authentication) {
		LOGGER.trace("enter authentication manager");
		String username = authentication.getPrincipal().toString();

	    User user = userRepo.findById(username)
	    		.orElseThrow(() -> new BadCredentialsException("no valid user name provided"));
	    if (user == null) {
			LOGGER.debug("user not found in repo");
	        throw new BadCredentialsException("no user name provided");
	    }
	    if (user.isDisabled()) {
	    	LOGGER.debug("user is disabled");
	        throw new DisabledException("user disabled");
	    }
	    String rawPassword = authentication.getCredentials().toString();
	    
	    if (!passwdEncoder.matches(rawPassword, user.getPassword())) {
	    	LOGGER.debug("password does not match");
	        throw new BadCredentialsException("user and/or password do not match");
	    }
	    LOGGER.trace("user successfully authenticated, will create UsernamePasswordAuthenticationToken...");
		List<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>() ;
		user.getFunctions().forEach(function ->  userAuthorities.add(new SimpleGrantedAuthority(function)));
	    return new UsernamePasswordAuthenticationToken(username, user.getPassword(), userAuthorities);

	}
	

}
