package io.sapl.demo.webflux.medical;

import org.springframework.stereotype.Service;

import io.sapl.spring.method.metadata.PreEnforce;
import reactor.core.publisher.Flux;

@Service
public class PatientsService {

	// @formatter:off
	private static final Patient[] PATIENTS = {
			new Patient("Phocas Zephyros",      "BA50",             "Past myocardial infarction diagnosed by ECG or other special investigation, but currently presenting no symptoms."),
			new Patient("Myrrhine Mattithyahu", "BB03",             "A postnatal pathological change in form or function of one or more pulmonary veins."),
			new Patient("Ilia Mamie",           "LA10",             "Any condition caused by failure of the ocular globes to correctly develop during the antenatal period."),
			new Patient("Arnold Velibor",       "NA0A.1",           "Injury of muscle, fascia or tendon of head"),
			new Patient("Wanjala Neptuno",      "NC92.2 XK8G Left", "Fracture of shaft of tibia with or without mention of fracture of fibula"),
			new Patient("Murat Taniqua",        "PE52 XE1C6",       "Assault by injury other than drowning while in body of water"),
			new Patient("Muhamad Dimitrij",     "4A01",             "Primary immunodeficiencies due to disorders of adaptive immunity"),
			new Patient("Shankar Lazarus",      "7A41",             "Obstructive sleep apnoea"),
			new Patient("Sameer Merida",        "9C60",             "Glaucoma suspect"),
			new Patient("Nedelko Iosaphat",     "AB14",             "Acute myringitis"),
			new Patient("Auðr Neve",            "EC21.1",           "Genetic syndromes with abnormalities of the hair shaft"),
			new Patient("Bharath Sevastian",    "FA01.0",           "Primary osteoarthritis of knee"),
	};
	// @formatter:on

	@PreEnforce
	public Flux<Patient> getPatients() {
		return Flux.fromArray(PATIENTS);
	}

}
