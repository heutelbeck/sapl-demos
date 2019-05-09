package org.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.demo.model.PatientListItem;
import org.demo.view.blocking.AbstractPatientForm;
import org.springframework.stereotype.Component;

import io.sapl.spring.method.pre.PreEnforce;
import lombok.RequiredArgsConstructor;

/**
 * This controller demonstrates the usage of the {@link PreEnforce @PreEnforce}
 * annotation.
 */
@Component
@RequiredArgsConstructor
public class UIController {

	private final PatientRepository patientRepo;

	@PreEnforce
	public List<PatientListItem> getPatients() {
		final List<PatientListItem> result = new ArrayList<>();
		final Iterable<Patient> allPatients = patientRepo.findAll();
		allPatients.forEach(patient -> result
				.add(new PatientListItem(patient.getId(), patient.getName())));
		return result;
	}

	@PreEnforce
	public Optional<Patient> getPatient(Long patientId) {
		return patientRepo.findById(patientId);
	}

	@PreEnforce
	public void createPatient(Patient patient) {
		patientRepo.save(patient);
	}

	@PreEnforce
	public void updatePatient(Patient patient, AbstractPatientForm form) {
		if (form.hasNameBeenModified()) {
			patientRepo.updateNameById(patient.getName(), patient.getId());
		}
		if (form.hasIcd11CodeBeenModified()) {
			patientRepo.updateIcd11CodeById(patient.getIcd11Code(), patient.getId());
		}
		if (form.hasDiagnosisTextBeenModified()) {
			patientRepo.updateDiagnosisTextById(patient.getDiagnosisText(),
					patient.getId());
		}
		if (form.hasAttendingDoctorBeenModified()) {
			patientRepo.updateAttendingDoctorById(patient.getAttendingDoctor(),
					patient.getId());
		}
		if (form.hasAttendingNurseBeenModified()) {
			patientRepo.updateAttendingNurseById(patient.getAttendingNurse(),
					patient.getId());
		}
		if (form.hasPhoneNumberBeenModified()) {
			patientRepo.updatePhoneNumberById(patient.getPhoneNumber(), patient.getId());
		}
		if (form.hasRoomNumberBeenModified()) {
			patientRepo.updateRoomNumberById(patient.getRoomNumber(), patient.getId());
		}
	}

	@PreEnforce
	public void deletePatient(Patient patient) {
		patientRepo.deleteById(patient.getId());
	}

}
