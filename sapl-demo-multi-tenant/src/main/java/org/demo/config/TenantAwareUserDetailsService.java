package org.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.demo.tenants.TenantAwareUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TenantAwareUserDetailsService implements UserDetailsService {

	Map<String, TenantAwareUserDetails> users = new HashMap<>();

	public void load(TenantAwareUserDetails user) {
		users.put(user.getUsername(), user);
	}

	@Override
	public TenantAwareUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!users.containsKey(username))
			throw new UsernameNotFoundException("no such user");
		return users.get(username);
	}

}
