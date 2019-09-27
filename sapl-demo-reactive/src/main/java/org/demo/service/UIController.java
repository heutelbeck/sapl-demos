package org.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.demo.model.PatientListItem;
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
		allPatients.forEach(patient -> result.add(new PatientListItem(patient.getId(), patient.getName())));
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
	public void updatePatient(Patient modifiedPatient, Patient originalPatient) {
		if (!Objects.equals(modifiedPatient.getName(), originalPatient.getName())) {
			patientRepo.updateNameById(modifiedPatient.getName(), modifiedPatient.getId());
		}
		if (!Objects.equals(modifiedPatient.getIcd11Code(), originalPatient.getIcd11Code())) {
			patientRepo.updateIcd11CodeById(modifiedPatient.getIcd11Code(), modifiedPatient.getId());
		}
		if (!Objects.equals(modifiedPatient.getDiagnosisText(), originalPatient.getDiagnosisText())) {
			patientRepo.updateDiagnosisTextById(modifiedPatient.getDiagnosisText(), modifiedPatient.getId());
		}
		if (!Objects.equals(modifiedPatient.getAttendingDoctor(), originalPatient.getAttendingDoctor())) {
			patientRepo.updateAttendingDoctorById(modifiedPatient.getAttendingDoctor(), modifiedPatient.getId());
		}
		if (!Objects.equals(modifiedPatient.getAttendingNurse(), originalPatient.getAttendingNurse())) {
			patientRepo.updateAttendingNurseById(modifiedPatient.getAttendingNurse(), modifiedPatient.getId());
		}
		if (!Objects.equals(modifiedPatient.getPhoneNumber(), originalPatient.getPhoneNumber())) {
			patientRepo.updatePhoneNumberById(modifiedPatient.getPhoneNumber(), modifiedPatient.getId());
		}
		if (!Objects.equals(modifiedPatient.getRoomNumber(), originalPatient.getRoomNumber())) {
			patientRepo.updateRoomNumberById(modifiedPatient.getRoomNumber(), modifiedPatient.getId());
		}
	}

	@PreEnforce
	public void deletePatient(Patient patient) {
		patientRepo.deleteById(patient.getId());
	}

}
