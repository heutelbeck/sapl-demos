package io.sapl.demo.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DemoData {

	private static final String HRN1 = "123456";
	private static final String HRN2 = "4711";
	private static final String ROLE_DOCTOR = "DOCTOR";
	private static final String ROLE_NURSE = "NURSE";
	private static final String ROLE_VISITOR = "VISITOR";
	private static final String ROLE_ADMIN = "ADMIN";
	private static final String NAME_DOMINIK = "Dominik";
	private static final String NAME_JULIA = "Julia";
	private static final String NAME_PETER = "Peter";
	private static final String NAME_ALINA = "Alina";
	private static final String NAME_THOMAS = "Thomas";
	private static final String NAME_BRIGITTE = "Brigitte";
	private static final String NAME_JANOSCH = "Janosch";
	private static final String NAME_JANINA = "Janina";
	private static final String NAME_LENNY = "Lenny";
	private static final String NAME_KARL = "Karl";
	private static final String NAME_HORST = "Horst";

	public static void loadDemoDataset(UserRepo userRepo, String password, PatientRepo patientRepo, RelationRepo relationRepo) {
		userRepo.save(new User(NAME_DOMINIK, password, false, new ArrayList<>(Collections.singletonList(ROLE_VISITOR))));
		userRepo.save(new User(NAME_JULIA, password, false, new ArrayList<>(Collections.singletonList(ROLE_DOCTOR))));
		userRepo.save(new User(NAME_PETER, password, false, new ArrayList<>(Collections.singletonList(ROLE_DOCTOR))));
		userRepo.save(new User(NAME_ALINA, password, false, new ArrayList<>(Collections.singletonList(ROLE_DOCTOR))));
		userRepo.save(new User(NAME_THOMAS, password, false, new ArrayList<>(Collections.singletonList(ROLE_NURSE))));
		userRepo.save(new User(NAME_BRIGITTE, password, false, new ArrayList<>(Collections.singletonList(ROLE_NURSE))));
		userRepo.save(new User(NAME_JANOSCH, password, false, new ArrayList<>(Collections.singletonList(ROLE_NURSE))));
		userRepo.save(new User(NAME_JANINA, password, false, new ArrayList<>(Collections.singletonList(ROLE_NURSE))));
		userRepo.save(new User(NAME_HORST, password, false, new ArrayList<>(Arrays.asList(ROLE_DOCTOR, ROLE_ADMIN))));

		patientRepo.save(new Patient(NAME_LENNY, "sick from working", HRN1, "111111111111", NAME_JULIA, NAME_THOMAS, "H264"));
		patientRepo.save(new Patient(NAME_KARL, "healthy", HRN2, "222222222222", NAME_ALINA, NAME_JANINA, "N333"));

		relationRepo.save(new Relation(NAME_DOMINIK, patientRepo.findByName(NAME_LENNY).getId()));
		relationRepo.save(new Relation(NAME_JULIA, patientRepo.findByName(NAME_KARL).getId()));
		relationRepo.save(new Relation(NAME_ALINA, patientRepo.findByName(NAME_KARL).getId()));
		relationRepo.save(new Relation(NAME_JANOSCH, patientRepo.findByName(NAME_KARL).getId()));
	}
}
