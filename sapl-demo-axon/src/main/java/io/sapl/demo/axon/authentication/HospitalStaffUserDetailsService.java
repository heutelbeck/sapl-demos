package io.sapl.demo.axon.authentication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class HospitalStaffUserDetailsService implements ReactiveUserDetailsService {

	Map<String, HospitalStaff> users = new HashMap<>();

	public void load(HospitalStaff user) {
		users.put(user.getUsername(), user);
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		var user = users.get(username);
		if (user == null) {
			return Mono.error(new UsernameNotFoundException("User not found"));
		}
		return Mono.just(user);
	}

}