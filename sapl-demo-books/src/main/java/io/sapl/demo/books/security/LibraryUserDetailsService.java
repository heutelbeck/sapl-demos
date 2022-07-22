package io.sapl.demo.books.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.sapl.demo.books.domain.LibraryUser;

@Service
public class LibraryUserDetailsService implements UserDetailsService {

	Map<String, LibraryUser> users = new HashMap<>();

	public void load(LibraryUser user) {
		users.put(user.getUsername(), user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return users.get(username);
	}

}
