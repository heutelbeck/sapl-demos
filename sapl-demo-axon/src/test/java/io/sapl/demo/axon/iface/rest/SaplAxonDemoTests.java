package io.sapl.demo.axon.iface.rest;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.test.StepVerifier.*;

import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
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
import io.sapl.demo.axon.data.DemoData;
import io.sapl.demo.axon.data.DemoData.DemoPatient;
import io.sapl.demo.axon.data.DemoData.DemoUser;
import io.sapl.demo.axon.query.patients.api.PatientDocument;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringJUnitConfig
@SpringBootTest(classes = SaplDemoAxonApplication.class)
@Import(io.sapl.demo.axon.iface.rest.SaplAxonDemoTests.TestConfiguration.class)
public class SaplAxonDemoTests {

	private static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(100);

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

	private static record UserNameAndPatientIdAndWard(String userName, String patientId, Ward ward) {
	}

	private static Collection<String> userNameSource() {
		return Stream.of(DemoData.DEMO_USERS).map(DemoUser::userName).toList();
	}

	private static Collection<DemoUser> doctorSource() {
		return Stream.of(DemoData.DEMO_USERS).filter(user -> user.position() == Position.DOCTOR).toList();
	}

	private static Collection<UserNameAndPatientId> userNameAndPatientIdSource() {
		return userNameSource().stream().flatMap(userName -> Stream.of(DemoData.DEMO_PATIENTS)
				.map(DemoPatient::patientId).map(patientId -> new UserNameAndPatientId(userName, patientId))).toList();
	}

	private static Collection<UserNameAndPatientIdAndWard> doctorsAndTheirPatients() {
		return doctorSource().stream()
				.flatMap(doctor -> Stream.of(DemoData.DEMO_PATIENTS)
						.filter(patient -> patient.doctor().equals(doctor.userName())).map(DemoPatient::patientId)
						.map(patientId -> new UserNameAndPatientIdAndWard(doctor.userName(), patientId, doctor.ward())))
				.toList();
	}

	private static void assertPatientBlackening(HospitalStaff staff, PatientDocument patient, boolean justNurses) {
		var codeBlackend = patient.latestIcd11Code().endsWith("█");
		var textBlackend = patient.latestDiagnosisText().endsWith("█");
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
		create(Mono.zip(pricipal, response))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), true)).verifyComplete();
	}

	@ParameterizedTest
	@MethodSource("userNameSource")
	public void fetchAllPatientsTest(String userName) {
		var pricipal = login(userDetailService.findByUsername(userName));
		var response = pricipal.then(controller.fetchAllPatients()).flatMapMany(Flux::fromIterable);
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
	public void fetchPatientTest(UserNameAndPatientId unapi) {
		var pricipal = login(userDetailService.findByUsername(unapi.userName()));
		var response = pricipal.then(controller.fetchPatient(unapi.patientId())).filter(ResponseEntity::hasBody)
				.map(ResponseEntity::getBody).map(body -> (PatientDocument) body);
		create(Mono.zip(pricipal, response))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), true)).verifyComplete();
	}

	@ParameterizedTest
	@MethodSource("doctorsAndTheirPatients")
	public void streamPatientAndHospitalizePatientTest(UserNameAndPatientIdAndWard unapiaw) {
		var pricipal = login(userDetailService.findByUsername(unapiaw.userName()));
		var response = pricipal.thenMany(controller.streamPatient(unapiaw.patientId())).map(ServerSentEvent::data)
				.map(data -> (PatientDocument) data);
		create(response)
				.assertNext(patient -> assertTrue(patient.ward() == unapiaw.ward() || patient.ward() == Ward.NONE
						|| patient.ward() == Ward.GENERAL))
				.then(() -> controller.hospitalizePatient(unapiaw.patientId(), unapiaw.ward()).subscribe())
				.assertNext(patient -> assertTrue(patient.ward() == unapiaw.ward()))
				.then(() -> controller.hospitalizePatient(unapiaw.patientId()).subscribe())
				.assertNext(patient -> assertTrue(patient.ward() == Ward.NONE)).verifyTimeout(DEFAULT_TIMEOUT);
	}

	private Mono<HospitalStaff> login(Mono<UserDetails> userPulisher) {
		return userPulisher.doOnNext(user -> {
			var testAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(),
					user.getAuthorities());
			authenticationSupplier.setTestAuthentication(testAuthentication);
		}).map(user -> (HospitalStaff) user);
	}

}
