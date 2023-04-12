package io.sapl.demo.axon.iface.rest;

import static io.sapl.demo.axon.authentication.Position.DOCTOR;
import static io.sapl.demo.axon.authentication.Position.NURSE;
import static io.sapl.demo.axon.command.patient.Ward.CCU;
import static io.sapl.demo.axon.command.patient.Ward.GENERAL;
import static io.sapl.demo.axon.command.patient.Ward.ICCU;
import static io.sapl.demo.axon.command.patient.Ward.SICU;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.axon.authentication.AuthnUtil;
import io.sapl.axon.authentication.reactive.ReactiveAuthenticationSupplier;
import io.sapl.axon.authentication.servlet.AuthenticationSupplier;
import io.sapl.axon.authentication.servlet.ServletAuthenticationSupplier;
import io.sapl.demo.axon.SaplDemoAxonApplication;
import io.sapl.demo.axon.authentication.HospitalStaffUserDetailsService;
import lombok.Setter;
import reactor.core.publisher.Mono;

@SpringJUnitConfig
@SpringBootTest(classes = SaplDemoAxonApplication.class)
@Import(io.sapl.demo.axon.iface.rest.PatientsControllerTests.TestConfiguration.class)
public class PatientsControllerTests {

	private static final String[] USER_NAMES = new String[] { "karl", "cheryl", "phyllis", "neil", "eleanore", "david",
			"donna" };

	@org.springframework.boot.test.context.TestConfiguration
	public static class TestConfiguration {
		@Bean
		@Primary
		public AuthenticationSupplier authenticationSupplier(ObjectMapper mapper) {
			return new TestAuthenticationSupplier(mapper);
		}

		@Bean
		@Primary
		public ReactiveAuthenticationSupplier reactiveAuthenticationSupplier(
				AuthenticationSupplier authenticationSupplier) {
			return () -> {
				return Mono.just(authenticationSupplier.get()).onErrorResume(e -> Mono.empty())
						.defaultIfEmpty("\"anonymous\"");
			};
		}
	}

	private static class TestAuthenticationSupplier extends ServletAuthenticationSupplier {
		private ObjectMapper mapper;
		@Setter
		private Authentication testAuthentication = null;

		public TestAuthenticationSupplier(ObjectMapper mapper) {
			super(mapper);
			this.mapper = mapper;
		}

		@Override
		public String get() {
			if (testAuthentication == null)
				return super.get();
			return AuthnUtil.authenticationToJsonString(testAuthentication, mapper);
		}
	}

	private static Collection<String> userNameSource() {
		return List.of(USER_NAMES);
	}

	@Autowired
	private HospitalStaffUserDetailsService userDetailService;

	@Autowired
	private PatientsController controller;

	@Autowired
	private TestAuthenticationSupplier authenticationSupplier;

	@BeforeEach
	public void beforeEach() {
		authenticationSupplier.setTestAuthentication(null);
	}

	// KARL
	@ParameterizedTest
	@MethodSource("userNameSource")
	public void fetchAllPatientsViaPublisherTest(String userName) {
		System.out.println("\n" + userName + "\n");
		login(userDetailService.findByUsername(userName))
				.thenMany(controller.fetchAllPatientsViaPublisher().doOnNext(System.out::println)).collectList()
				.block();
	}

	// END KARL

	private Mono<Void> login(Mono<UserDetails> userPulisher) {
		return userPulisher.doOnNext(user -> {
			var testAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(),
					user.getAuthorities());
			authenticationSupplier.setTestAuthentication(testAuthentication);
		}).then();
	}

}
