package io.sapl.demo.axon.iface.rest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import io.sapl.demo.axon.SaplDemoAxonApplication;
import io.sapl.demo.axon.authentication.HospitalStaffUserDetailsService;
import reactor.test.StepVerifier;

@SpringJUnitConfig
@SpringBootTest(classes = SaplDemoAxonApplication.class)
public class PatientsControllerTests {

	private static void login(UserDetails user) {
		var authentication = UsernamePasswordAuthenticationToken.authenticated(user.getUsername(), user.getPassword(),
				user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Autowired
	private HospitalStaffUserDetailsService userDetailService;

	@Autowired
	private PatientsController controller;

	
	// KARL
	@Test
	@Disabled // shouldn't deny access
	public void fetchAllPatientsViaPublisherTest() throws Exception {
		login(userDetailService.findByUsername("karl").block());
		StepVerifier.create(controller.fetchAllPatientsViaPublisher());
	}
	
	// END KARL

}
