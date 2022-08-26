package io.sapl.demo.axon.data;

import static io.sapl.demo.axon.command.Position.ADMINISTRATOR;
import static io.sapl.demo.axon.command.Position.DOCTOR;
import static io.sapl.demo.axon.command.Position.NURSE;
import static io.sapl.demo.axon.command.Ward.CCU;
import static io.sapl.demo.axon.command.Ward.GENERAL;
import static io.sapl.demo.axon.command.Ward.ICCU;
import static io.sapl.demo.axon.command.Ward.NONE;
import static io.sapl.demo.axon.command.Ward.SICU;

import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.heutelbeck.uuid.Base64Id;

import io.sapl.demo.axon.adapter.monitors.MonitorFactory;
import io.sapl.demo.axon.authentication.HospitalStaff;
import io.sapl.demo.axon.authentication.HospitalStappUserDetailsService;
import io.sapl.demo.axon.command.PatientCommandAPI.ConnectMonitorToPatient;
import io.sapl.demo.axon.command.PatientCommandAPI.RegisterPatient;
import io.sapl.demo.axon.command.Position;
import io.sapl.demo.axon.command.Ward;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoData implements ApplicationListener<ContextRefreshedEvent> {

	private static final int    NUMBER_OF_PATIENTS = 10;
	private static final String PASSWORD           = "pwd";
	private static final String NOOP               = "{noop}";

	private final ReactorCommandGateway           commandGateway;
	private final HospitalStappUserDetailsService userDetailsService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		generatePatients();
		generateHospitalStaff();
	}

	// @formatter:off
	private void generateHospitalStaff() {
		log.info("");
		log.info("This demo is pre-configured with the follwoing users:");
		log.info("");
		log.info(" Username   | Password | Position       | Ward");
		log.info("------------------------------------------------------------------------------");
		loadUser("karl",        PASSWORD,  NURSE,           ICCU);
		loadUser("cheryl",      PASSWORD,  DOCTOR,          ICCU);
		loadUser("phyllis",     PASSWORD,  NURSE,           CCU);
		loadUser("neil",        PASSWORD,  DOCTOR,          CCU);
		loadUser("eleanore",    PASSWORD,  NURSE,           GENERAL);
		loadUser("david",       PASSWORD,  DOCTOR,          SICU);
		loadUser("donna",       PASSWORD,  ADMINISTRATOR,   NONE);
		log.info("");
	}
	// @formatter:on

	private void loadUser(String username, String rawPassword, Position position, Ward ward) {
		log.info(String.format(" %-10.10s | %-8.8s | %-14.14s | %s ", username, rawPassword, position,
				ward.getDescription() + " (" + ward + ")"));
		userDetailsService.load(new HospitalStaff(username, ward, position, NOOP + rawPassword));
	}

	private void generatePatients() {
		var generator = new NameGenerator();
		var names     = generator.generateNames(NUMBER_OF_PATIENTS);
		names.stream().forEach(this::createPatientWithName);
	}

	void createPatientWithName(Name name) {
		// @formatter:off
		commandGateway
			.send(new RegisterPatient(Base64Id.randomID(), name.toString())).cast(String.class)	
			.flatMap(id -> newMonitor(id,MonitorFactory.HEART_RATE)
							.then(newMonitor(id,MonitorFactory.BLOOD_PRESSURE))
							.then(newMonitor(id,MonitorFactory.BODY_TEMPERATURE))
							.then(newMonitor(id,MonitorFactory.RESPIRATION_RATE)))
			.subscribe();
		// @formatter:on
	}

	Mono<Object> newMonitor(String patientId, String type) {
		return commandGateway.send(new ConnectMonitorToPatient(patientId, Base64Id.randomID(), type));
	}

}
