package io.sapl.demo.axon.data;

import static io.sapl.demo.axon.authentication.Position.ADMINISTRATOR;
import static io.sapl.demo.axon.authentication.Position.DOCTOR;
import static io.sapl.demo.axon.authentication.Position.NURSE;
import static io.sapl.demo.axon.command.MonitorType.BLOOD_PRESSURE;
import static io.sapl.demo.axon.command.MonitorType.BODY_TEMPERATURE;
import static io.sapl.demo.axon.command.MonitorType.HEART_RATE;
import static io.sapl.demo.axon.command.MonitorType.RESPIRATION_RATE;
import static io.sapl.demo.axon.command.patient.Ward.CCU;
import static io.sapl.demo.axon.command.patient.Ward.GENERAL;
import static io.sapl.demo.axon.command.patient.Ward.ICCU;
import static io.sapl.demo.axon.command.patient.Ward.NONE;
import static io.sapl.demo.axon.command.patient.Ward.SICU;

import java.util.List;

import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.heutelbeck.uuid.Base64Id;

import io.sapl.demo.axon.authentication.HospitalStaff;
import io.sapl.demo.axon.authentication.HospitalStaffUserDetailsService;
import io.sapl.demo.axon.authentication.Position;
import io.sapl.demo.axon.command.MonitorType;
import io.sapl.demo.axon.command.patient.Ward;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.ConnectMonitorToPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.HospitalisePatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.MakeDiagnosisForPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.RegisterPatient;
import io.sapl.demo.axon.configuration.ImpersonationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoData implements ApplicationListener<ContextRefreshedEvent> {

	private static final String                   PASSWORD  = "pwd";
	private static final String                   NOOP      = "{noop}";
	private static final String[]                 ICD11     = { "1B21.3", "1B95", "1E31", "2A90.7", "6A25.3", "6C73",
			"6D51", "6E40.2", "9A82", "9A96.2", "9B02.2", "AB11.1", "BD11.1", "BC02.30", "DA20", "DC10", "LB70.1",
			"LB79.0", "LB99.2", "NA01.3", "NA01.5", "PF13", "PJ00" };
	private static final String[]                 DIAGNOSIS = { "Disseminated non-tuberculous mycobacterial infection",
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
	private final HospitalStaffUserDetailsService userDetailsService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		generateHospitalStaff();
		generatePatients();
	}

	// @formatter:off
	private void generateHospitalStaff() {
		log.info("");
		log.info("This demo is pre-configured with the following users:");
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
		log.info("");
		log.info("This demo is pre-configured with the following patients:");
		log.info("");
		log.info(" Id  | Name                 | Ward");
		log.info("-----+----------------------+-------------------------------------");
		// @formatter:off
		loadPatient("0","Mona Vance",       List.of(HEART_RATE, BLOOD_PRESSURE, BODY_TEMPERATURE, RESPIRATION_RATE), ICCU,    "cheryl",ICD11[1], DIAGNOSIS[1]  );
		loadPatient("1","Martin Pape",      List.of(HEART_RATE, BLOOD_PRESSURE),                                     ICCU,    "cheryl",ICD11[4], DIAGNOSIS[4]  );
		loadPatient("2","Richard Lewis",    List.of(HEART_RATE,                 BODY_TEMPERATURE, RESPIRATION_RATE), CCU,     "neil",  ICD11[6], DIAGNOSIS[6]  );
		loadPatient("3","Jesse Ramos",      List.of(            BLOOD_PRESSURE,                   RESPIRATION_RATE), CCU,     "neil",  ICD11[8], DIAGNOSIS[8]  );
		loadPatient("4","Lester Romaniak",  List.of(HEART_RATE, BLOOD_PRESSURE, BODY_TEMPERATURE, RESPIRATION_RATE), CCU,     "neil",  ICD11[11],DIAGNOSIS[11] );
		loadPatient("5","Matthew Cortazar", List.of(HEART_RATE,                                   RESPIRATION_RATE), SICU,    "david", ICD11[15],DIAGNOSIS[15] );
		loadPatient("6","Timothy Favero",   List.of(HEART_RATE, BLOOD_PRESSURE, BODY_TEMPERATURE, RESPIRATION_RATE), SICU,    "david", ICD11[17],DIAGNOSIS[17] );
		loadPatient("7","Louise Colley",    List.of(                                              RESPIRATION_RATE), GENERAL, "david", ICD11[22],DIAGNOSIS[22] );
		loadPatient("8","Bret Gerson",      List.of(                            BODY_TEMPERATURE                  ), ICCU,    "cheryl",ICD11[7], DIAGNOSIS[7]  );
		loadPatient("9","Richard Spreer",   List.of(            BLOOD_PRESSURE                                    ), NONE,    "cheryl",ICD11[19],DIAGNOSIS[19] );
		// @formatter:on
		log.info("");
	}

	private void loadPatient(String patientId, String name, List<MonitorType> monitors, Ward ward, String doctor,
			String icd11, String diagnosis) {
		log.info(String.format(" %-3.3s | %-20.20s | %s ", patientId, name, ward.getDescription() + " (" + ward + ")"));
		var creationProcess = commandGateway.send(new RegisterPatient(patientId, name.toString())).cast(String.class);
		creationProcess = creationProcess.then(commandGateway.send(new HospitalisePatient(patientId, ward)));
		for (var monitor : monitors) {
			creationProcess = creationProcess
					.then(commandGateway.send(new ConnectMonitorToPatient(patientId, Base64Id.randomID(), monitor)));
		}
		creationProcess = creationProcess
				.then(commandGateway.send(new MakeDiagnosisForPatient(patientId, doctor, icd11, diagnosis)));
		creationProcess.contextWrite(ReactiveSecurityContextHolder.withAuthentication(ImpersonationUtil.systemUser()))
				.subscribe();
	}

}
