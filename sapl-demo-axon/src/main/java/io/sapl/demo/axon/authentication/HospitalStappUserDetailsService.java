package io.sapl.demo.axon.authentication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class HospitalStappUserDetailsService implements UserDetailsService {

	Map<String, HospitalStaff> users = new HashMap<>();

	public void load(HospitalStaff user) {
		users.put(user.getUsername(), user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = users.get(username);
		if(user==null) {
			throw new UsernameNotFoundException("User not found");
		}
		return user;
	}

}