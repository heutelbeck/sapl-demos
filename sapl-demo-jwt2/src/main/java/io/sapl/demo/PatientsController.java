package io.sapl.demo;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.spring.annotation.PdpAuthorize;

@RestController
@RequestMapping("/patients")
public class PatientsController {

	@Autowired
	private PatientRepo patientRepo;

	@GetMapping // permission to all users: VISITOR, DOCTOR, NURSE, ADMIN
	public Iterable<Patient> readPatientsList() {
		return patientRepo.findAll();
	}

	@PdpAuthorize
	@GetMapping("{id}")
	public Patient readPatient(@PathVariable int id) {
		return patientRepo.findById(id).orElseThrow(PersonNotFound::new);
	}

	@DeleteMapping("{id}")
	public void deletePerson(@PathVariable int id, HttpServletRequest request) {
		patientRepo.deleteById(id);
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public static class PersonNotFound extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public PersonNotFound() {
			super();
		}

		public PersonNotFound(String message, Throwable cause) {
			super(message, cause);
		}

		public PersonNotFound(String message) {
			super(message);
		}

		public PersonNotFound(Throwable cause) {
			super(cause);
		}
	}
}
