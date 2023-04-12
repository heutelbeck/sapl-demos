package io.sapl.demo.axon.iface.rest;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.test.StepVerifier.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
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
import io.sapl.demo.axon.authentication.HospitalStaff;
import io.sapl.demo.axon.authentication.HospitalStaffUserDetailsService;
import io.sapl.demo.axon.authentication.Position;
import io.sapl.demo.axon.command.patient.Ward;
import io.sapl.demo.axon.query.patients.api.PatientDocument;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@SpringJUnitConfig
@SpringBootTest(classes = SaplDemoAxonApplication.class)
@Import(io.sapl.demo.axon.iface.rest.PatientsTests.TestConfiguration.class)
public class PatientsTests {

	private static final String[] USER_NAMES = new String[] { "karl", "cheryl", "phyllis", "neil", "eleanore", "david",
			"donna" };
	private static final String[] PATIENT_IDS = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

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

	private static record UserNameAndPatientId(String userName, String patientId) {
	}

	private static Collection<String> userNameSource() {
		return List.of(USER_NAMES);
	}

	private static Collection<UserNameAndPatientId> userNameAndPatientIdSource() {
		return List.of(USER_NAMES).stream().flatMap(userName -> List.of(PATIENT_IDS).stream()
				.map(patientId -> new UserNameAndPatientId(userName, patientId))).toList();
	}

	private static void assertPatientBlackening(HospitalStaff staff, PatientDocument patient, boolean justNurses) {
		var codeBlackend = patient.latestIcd11Code().endsWith("█");
		var textBlackend = patient.latestDiagnosisText().endsWith("█");
		System.out.println(staff + "\n" + patient + "\n" + codeBlackend + "\t" + textBlackend);
		if (staff.getPosition() == Position.DOCTOR || ((staff.getPosition() == Position.NURSE || !justNurses)
				&& staff.getAssignedWard() == patient.ward()))
			assertFalse(codeBlackend || textBlackend); // both must be false
		else
			assertTrue(codeBlackend && textBlackend); // both must be true
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

	@ParameterizedTest
	@MethodSource("userNameSource")
	public void fetchAllPatientsViaPublisherTest(String userName) {
		var pricipal = login(userDetailService.findByUsername(userName));
		var response = pricipal.thenMany(controller.fetchAllPatientsViaPublisher());
		create(Flux.zip(pricipal.repeat(9), response))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), false)).verifyComplete();
	}

	@ParameterizedTest
	@MethodSource("userNameAndPatientIdSource")
	public void fetchPatientViaPublisherTest(UserNameAndPatientId unapi) {
		var pricipal = login(userDetailService.findByUsername(unapi.userName()));
		var response = pricipal.then(controller.fetchPatientViaPublisher(unapi.patientId()))
				.filter(ResponseEntity::hasBody).map(ResponseEntity::getBody).map(body -> (PatientDocument) body);
		create(Mono.zip(pricipal, response)).assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), true))
				.verifyComplete();
	}

	private Mono<HospitalStaff> login(Mono<UserDetails> userPulisher) {
		return userPulisher.doOnNext(user -> {
			var testAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(),
					user.getAuthorities());
			authenticationSupplier.setTestAuthentication(testAuthentication);
		}).map(user -> (HospitalStaff) user);
	}

}
