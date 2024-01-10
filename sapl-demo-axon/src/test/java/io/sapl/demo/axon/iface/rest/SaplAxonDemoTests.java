package io.sapl.demo.axon.iface.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static reactor.test.StepVerifier.create;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
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
import io.sapl.demo.axon.command.MonitorType;
import io.sapl.demo.axon.command.patient.Ward;
import io.sapl.demo.axon.data.DemoData;
import io.sapl.demo.axon.data.DemoData.DemoPatient;
import io.sapl.demo.axon.data.DemoData.DemoUser;
import io.sapl.demo.axon.query.patients.api.PatientDocument;
import io.sapl.demo.axon.query.vitals.api.VitalSignMeasurement;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringJUnitConfig
@SpringBootTest(classes = SaplDemoAxonApplication.class)
@Import(io.sapl.demo.axon.iface.rest.SaplAxonDemoTests.TestConfiguration.class)
class SaplAxonDemoTests {

	private static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(500);

	@BeforeAll
	static void beforeAll() {
        System.setProperty("os.arch", "x86_64"); // fixes problem with flapdoodle misidentifying OS to be 32bit
	}
	
	@org.springframework.boot.test.context.TestConfiguration
	public static class TestConfiguration {
		@Bean
		@Primary
		AuthenticationSupplier authenticationSupplier(ObjectMapper mapper) {
			return new TestAuthenticationSupplier(mapper);
		}

		@Bean
		@Primary
		ReactiveAuthenticationSupplier reactiveAuthenticationSupplier(AuthenticationSupplier authenticationSupplier) {
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

	private static record UserNameAndPatientIdAndMonitorType(String userName, String patientId,
			MonitorType monitorType) {
	}

	private static <T> Function<T, T> cast(Class<T> clazz) {
		return Function.identity();
	}

	private static Collection<String> userNameSource() {
		return Stream.of(DemoData.DEMO_USERS).map(DemoUser::userName).toList();
	}

	private static Collection<DemoUser> doctorSource() {
		return Stream.of(DemoData.DEMO_USERS).filter(user -> user.position() == Position.DOCTOR).toList();
	}

	private static Collection<DemoUser> nurseSource() {
		return Stream.of(DemoData.DEMO_USERS).filter(user -> user.position() == Position.NURSE).toList();
	}

	private static Collection<UserNameAndPatientId> userNameAndPatientIdSource() {
		return userNameSource().stream().flatMap(userName -> Stream.of(DemoData.DEMO_PATIENTS)
				.map(DemoPatient::patientId).map(patientId -> new UserNameAndPatientId(userName, patientId))).toList();
	}

	private static Collection<UserNameAndPatientId> doctorAndPatientIdSource() {
		return doctorSource()
				.stream().map(DemoUser::userName).flatMap(userName -> Stream.of(DemoData.DEMO_PATIENTS)
						.map(DemoPatient::patientId).map(patientId -> new UserNameAndPatientId(userName, patientId)))
				.toList();
	}

	private static Collection<UserNameAndPatientIdAndWard> doctorsAndTheirPatients() {
		return doctorSource().stream()
				.flatMap(doctor -> Stream.of(DemoData.DEMO_PATIENTS)
						.filter(patient -> patient.doctor().equals(doctor.userName())).map(DemoPatient::patientId)
						.map(patientId -> new UserNameAndPatientIdAndWard(doctor.userName(), patientId, doctor.ward())))
				.toList();
	}

	private static Collection<UserNameAndPatientIdAndMonitorType> doctorsAndPatientsAndTheirMonitors() {
		return doctorAndPatientIdSource().stream()
				.flatMap(doctorAndPatient -> Stream.of(DemoData.DEMO_PATIENTS)
						.filter(demoPatient -> demoPatient.patientId().equals(doctorAndPatient.patientId)).findAny()
						.map(DemoPatient::monitors).map(List::stream).orElse(Stream.of())
						.map(monitorType -> new UserNameAndPatientIdAndMonitorType(doctorAndPatient.userName,
								doctorAndPatient.patientId, monitorType)))
				.toList();
	}

	private static Collection<UserNameAndPatientId> nursesAndPatientsWithBodyTemperatureMonitor() {
		return nurseSource().stream().map(DemoUser::userName)
				.flatMap(userName -> Stream.of(DemoData.DEMO_PATIENTS)
						.filter(demoPatient -> demoPatient.monitors().contains(MonitorType.BODY_TEMPERATURE))
						.map(DemoPatient::patientId).map(patientId -> new UserNameAndPatientId(userName, patientId)))
				.toList();
	}

	private static Collection<UserNameAndPatientIdAndMonitorType> nursesAndPatientsWithRawMonitor() {
		return nurseSource().stream().map(DemoUser::userName).flatMap(userName -> Stream.of(MonitorType.values())
				.filter(monitorType -> monitorType != MonitorType.BLOOD_PRESSURE)
				.filter(monitorType -> monitorType != MonitorType.BODY_TEMPERATURE)
				.flatMap(monitorType -> Stream.of(DemoData.DEMO_PATIENTS)
						.filter(demoPatient -> demoPatient.monitors().contains(monitorType)).map(DemoPatient::patientId)
						.map(patientId -> new UserNameAndPatientIdAndMonitorType(userName, patientId, monitorType))))
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
	private PatientsController patientsController;

	@Autowired
	private VitalSignsController vitalsController;

	@Autowired
	private TestAuthenticationSupplier authenticationSupplier;

	@BeforeEach
	public void beforeEach() {
		authenticationSupplier.setTestAuthentication(null);
	}

	@ParameterizedTest
	@MethodSource("userNameSource")
	void fetchAllPatientsViaPublisherTest(String userName) {
		var pricipal = login(userDetailService.findByUsername(userName));
		var response = pricipal.thenMany(patientsController.fetchAllPatientsViaPublisher());
    
		create(Flux.zip(pricipal.repeat(9), response).timeout(DEFAULT_TIMEOUT))
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
	void fetchPatientViaPublisherTest(UserNameAndPatientId unapi) {
		var pricipal = login(userDetailService.findByUsername(unapi.userName()));
		var response = pricipal.then(patientsController.fetchPatientViaPublisher(unapi.patientId()))
				.filter(ResponseEntity::hasBody).map(ResponseEntity::getBody).map(cast(PatientDocument.class));
		create(Mono.zip(pricipal, response).timeout(DEFAULT_TIMEOUT))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), true)).verifyComplete();
	}

	@ParameterizedTest
	@MethodSource("userNameSource")
	void fetchAllPatientsTest(String userName) {
		var pricipal = login(userDetailService.findByUsername(userName));
		var response = pricipal.then(patientsController.fetchAllPatients()).flatMapMany(Flux::fromIterable);
		create(Flux.zip(pricipal.repeat(9), response).timeout(DEFAULT_TIMEOUT))
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
	void fetchPatientTest(UserNameAndPatientId unapi) {
		var pricipal = login(userDetailService.findByUsername(unapi.userName()));
		var response = pricipal.then(patientsController.fetchPatient(unapi.patientId())).filter(ResponseEntity::hasBody)
				.map(ResponseEntity::getBody).map(cast(PatientDocument.class));
		create(Mono.zip(pricipal, response).timeout(DEFAULT_TIMEOUT))
				.assertNext(tuple -> assertPatientBlackening(tuple.getT1(), tuple.getT2(), true)).verifyComplete();
	}

	@ParameterizedTest
	@MethodSource("doctorsAndTheirPatients")
	void streamPatientAndHospitalizePatientTest(UserNameAndPatientIdAndWard unapiaw) {
		var pricipal = login(userDetailService.findByUsername(unapiaw.userName()));
		var response = pricipal.thenMany(patientsController.streamPatient(unapiaw.patientId()))
				.map(ServerSentEvent::data).map(cast(PatientDocument.class));
		create(response.timeout(DEFAULT_TIMEOUT))
				.assertNext(patient -> assertTrue(patient.ward() == unapiaw.ward() || patient.ward() == Ward.NONE
						|| patient.ward() == Ward.GENERAL))
				.then(() -> patientsController.hospitalizePatient(unapiaw.patientId(), unapiaw.ward()).subscribe())
				.assertNext(patient -> assertSame(unapiaw.ward(), patient.ward()))
				.then(() -> patientsController.hospitalizePatient(unapiaw.patientId()).subscribe())
				.assertNext(patient -> assertSame(Ward.NONE, patient.ward())).verifyTimeout(DEFAULT_TIMEOUT);
	}

	@ParameterizedTest
	@MethodSource("doctorsAndPatientsAndTheirMonitors")
	void fetchVitalsTestForDoctors(UserNameAndPatientIdAndMonitorType unapiamt) {
		var pricipal = login(userDetailService.findByUsername(unapiamt.userName()));
		var response = pricipal.then(vitalsController.fetchVitals(unapiamt.patientId(), unapiamt.monitorType()))
				.filter(ResponseEntity::hasBody).map(ResponseEntity::getBody).map(cast(VitalSignMeasurement.class));
		create(response.timeout(DEFAULT_TIMEOUT)).assertNext(measurement -> {
			assertFalse(measurement.monitorDeviceId().isBlank(), "expected id");
			assertFalse(measurement.monitorDeviceId().contains("█"), "expected non blackend id");
			assertNotNull(measurement.timestamp(), "expected timestamp");
			assertNotNull(measurement.type(), "expected monitor type");
			assertFalse(measurement.unit().isBlank(), "expected unit");
			assertFalse(measurement.unit().contains("█"), "expected non blackend unit");
			assertNotEquals("Body Temperature Category", measurement.unit(), "expected no body temperature category");
			assertFalse(measurement.value().isBlank(), "expected value");
			assertFalse(measurement.value().contains("█"), "expected non blackend value");
			assertNotEquals("Normal", measurement.value(), "expected normal");
		}).verifyComplete();
	}

	// omit testing time PIP

	@ParameterizedTest
	@MethodSource("nursesAndPatientsWithBodyTemperatureMonitor")
	void fetchVitalsTestForNursesAndBodyTemperature(UserNameAndPatientId unapi) {
		var pricipal = login(userDetailService.findByUsername(unapi.userName()));
		var response = pricipal.then(vitalsController.fetchVitals(unapi.patientId(), MonitorType.BODY_TEMPERATURE))
				.filter(ResponseEntity::hasBody).map(ResponseEntity::getBody).map(cast(VitalSignMeasurement.class));
		create(response.timeout(DEFAULT_TIMEOUT)).assertNext(measurement -> {
			assertFalse(measurement.monitorDeviceId().isBlank(), "expected id");
			assertFalse(measurement.monitorDeviceId().contains("█"), "expected non blackend id");
			assertNotNull(measurement.timestamp(), "expected timestamp");
			assertEquals(MonitorType.BODY_TEMPERATURE, measurement.type(), "expected body temerature");
			assertEquals("Body Temperature Category", measurement.unit(), "expected body temperature category");
			assertEquals("Normal", measurement.value(), "expected normal");
		}).verifyComplete();
	}

	@ParameterizedTest
	@MethodSource("nursesAndPatientsWithRawMonitor")
	void fetchVitalsTestForNursesAndRawMonitor(UserNameAndPatientIdAndMonitorType unapiamt) {
		var pricipal = login(userDetailService.findByUsername(unapiamt.userName()));
		var response = pricipal.then(vitalsController.fetchVitals(unapiamt.patientId(), unapiamt.monitorType()))
				.filter(ResponseEntity::hasBody).map(ResponseEntity::getBody).map(cast(VitalSignMeasurement.class));
		create(response.timeout(DEFAULT_TIMEOUT)).assertNext(measurement -> {
			assertFalse(measurement.monitorDeviceId().isBlank(), "expected id");
			assertFalse(measurement.monitorDeviceId().contains("█"), "expected non blackend id");
			assertNotNull(measurement.timestamp(), "expected timestamp");
			assertNotNull(measurement.type(), "expected monitor type");
			assertFalse(measurement.unit().isBlank(), "expected unit");
			assertFalse(measurement.unit().contains("█"), "expected non blackend unit");
			assertNotEquals("Body Temperature Category", measurement.unit(), "expected no body temperature category");
			assertFalse(measurement.value().isBlank(), "expected value");
			assertFalse(measurement.value().contains("█"), "expected non blackend value");
			assertNotEquals("Normal", measurement.value(), "expected normal");
		}).verifyComplete();
	}

	private Mono<HospitalStaff> login(Mono<UserDetails> userPulisher) {
		return userPulisher.doOnNext(user -> {
			var testAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(),
					user.getAuthorities());
			authenticationSupplier.setTestAuthentication(testAuthentication);
		}).map(user -> (HospitalStaff) user);
	}

}
