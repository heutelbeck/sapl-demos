package io.sapl.demo.repository;

import java.security.SecureRandom;

import org.springframework.data.repository.CrudRepository;

import io.sapl.demo.domain.Patient;

public interface PatientenRepo extends CrudRepository<Patient, Integer> {

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
