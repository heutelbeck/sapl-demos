package io.sapl.demo.axon.data;

import static io.sapl.demo.axon.command.Position.ADMINISTRATOR;
import static io.sapl.demo.axon.command.Position.DOCTOR;
import static io.sapl.demo.axon.command.Position.NURSE;
import static io.sapl.demo.axon.command.Ward.CCU;
import static io.sapl.demo.axon.command.Ward.GENERAL;
import static io.sapl.demo.axon.command.Ward.ICCU;
import static io.sapl.demo.axon.command.Ward.NONE;
import static io.sapl.demo.axon.command.Ward.SICU;

import java.util.random.RandomGenerator;

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
import io.sapl.demo.axon.command.PatientCommandAPI.MakeDiagnosisForPatient;
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
	private final static RandomGenerator RANDOM             = RandomGenerator.getDefault();
	private static final int             NUMBER_OF_PATIENTS = 10;
	private static final String          PASSWORD           = "pwd";
	private static final String          NOOP               = "{noop}";
	private static final String[]        DOCTORS            = { "cheryl", "neil", "david" };
	private static final String[]        ICD11              = { "1B21.3", "1B95", "1E31", "2A90.7", "6A25.3", "6C73",
			"6D51", "6E40.2", "9A82", "9A96.2", "9B02.2", "AB11.1", "BD11.1", "BC02.30", "DA20", "DC10", "LB70.1",
			"LB79.0", "LB99.2", "NA01.3", "NA01.5", "PF13", "PJ00" };
	private static final String[]        DIAGNOSIS          = { "Disseminated non-tuberculous mycobacterial infection",
			"Brucellosis", "Influenza due to identified zoonotic or pandemic influenza virus",
			"Enteropathy associated T-cell lymphoma", "Manic mood symptoms in primary psychotic disorders",
			"Intermittent explosive disorder", "Factitious disorder imposed on another",
			"Personality traits or coping style affecting disorders or diseases classified elsewhere",
			"Cyst in the anterior chamber of the eye", "Infection-associated anterior uveitis",
			"Mesencephalic light-near dissociations", "Chronic mastoiditis",
			"Left ventricular failure with mid range ejection fraction",
			"Stenosis of the neoaortic valve of pulmonary origin", "Acquired anatomical alterations of the oesophagus",
			"Acquired anatomical alterations of gallbladder or bile ducts", "Wormian bones", "Fused fingers",
			"Radial hemimelia", "Laceration with foreign body of head", "Puncture wound with foreign body of head",
			"Assault by exposure to radiation", "Victim of lightning" };

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
		log.info("------------+----------+----------------+-------------------------------------");
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
		var i      = RANDOM.nextInt(ICD11.length);
		var doctor = DOCTORS[RANDOM.nextInt(DOCTORS.length)];
		// @formatter:off
		commandGateway
			.send(new RegisterPatient(Base64Id.randomID(), name.toString())).cast(String.class)	
			.flatMap(id -> newMonitor(id,MonitorFactory.HEART_RATE)
							.then(newMonitor(id,MonitorFactory.BLOOD_PRESSURE))
							.then(newMonitor(id,MonitorFactory.BODY_TEMPERATURE))
							.then(newMonitor(id,MonitorFactory.RESPIRATION_RATE))
							.then(commandGateway.send(new MakeDiagnosisForPatient(id, doctor,  ICD11[i], DIAGNOSIS[i])))
							)
			.subscribe();
		// @formatter:on
	}

	Mono<Object> newMonitor(String patientId, String type) {
		return commandGateway.send(new ConnectMonitorToPatient(patientId, Base64Id.randomID(), type));
	}

}
