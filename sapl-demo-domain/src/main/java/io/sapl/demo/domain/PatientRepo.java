package io.sapl.demo.domain;

import java.security.SecureRandom;

import org.springframework.data.repository.CrudRepository;

public interface PatientRepo extends CrudRepository<Patient, Integer> {

	default Integer randomPatientIdNotInRepo() {
		SecureRandom random = new SecureRandom();
		Integer id = random.nextInt();
		while (existsById(id) || id < 0) {
			id = random.nextInt();
		}
		return id;
	}

	Patient findByName(String string);

}
