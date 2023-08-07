package io.sapl.pdp.multitenenat.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.sapl.pdp.multitenenat.demo.tenants.TenantAwareUserDetails;

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
