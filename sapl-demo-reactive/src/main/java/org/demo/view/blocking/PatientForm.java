package org.demo.view.blocking;

import java.util.Objects;
import java.util.Optional;

import org.demo.domain.PatientRepository;
import org.demo.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.spring.PolicyEnforcementPoint;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PatientForm extends AbstractPatientForm {

	private final RefreshCallback refreshCallback;
	private final PatientRepository patientRepo;
	private final PolicyEnforcementPoint pep;
	private final PolicyDecisionPoint pdp;

	protected void updateFieldEnabling(boolean isNewPatient) {
		id.setEnabled(false);
		blackenedHRN.setEnabled(false);

		if (isNewPatient) {
			name.setEnabled(true);
			diagnosis.setEnabled(false); // only the admin can create a new patient, but doctors add a diagnosis
			healthRecordNumber.setEnabled(true);
			phoneNumber.setEnabled(true);
			attendingDoctor.setEnabled(true);
			attendingNurse.setEnabled(true);
			roomNumber.setEnabled(true);
		} else {
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			name.setEnabled(pep.enforce(authentication, "update", "name"));
			diagnosis.setEnabled(pep.enforce(authentication, "updateDiagnosis", patient));
			healthRecordNumber.setEnabled(pep.enforce(authentication, "update", "HRN"));
			phoneNumber.setEnabled(pep.enforce(authentication, "update", "phoneNumber"));
			attendingDoctor.setEnabled(pep.enforce(authentication, "update", "attendingDoctor"));
			attendingNurse.setEnabled(pep.enforce(authentication, "update", "attendingNurse"));
			roomNumber.setEnabled(pep.enforce(authentication, "update", "roomNumber"));
		}
	}

	protected void updateFieldVisibility(boolean isNewPatient) {
		if (isNewPatient) {
			id.setVisible(false);
			name.setVisible(true);
			diagnosis.setVisible(false); // only the admin can create a new patient, but doctors add a diagnosis
			healthRecordNumber.setVisible(true);
			blackenedHRN.setVisible(false);
			phoneNumber.setVisible(true);
			attendingDoctor.setVisible(true);
			attendingNurse.setVisible(true);
			roomNumber.setVisible(true);
		} else {
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			id.setVisible(true);
			name.setVisible(pep.enforce(authentication, "read", "name"));
			diagnosis.setVisible(pep.enforce(authentication, "read", "diagnosis"));
			healthRecordNumber.setVisible(pep.enforce(authentication, "read", "HRN"));
			final Response readBlackenedHRN = pdp.decide(buildRequest(authentication, "readBlackenedHRN", patient))
					.block();
			if (readBlackenedHRN.getDecision() == Decision.PERMIT) {
				final Optional<JsonNode> patientNode = readBlackenedHRN.getResource();
				blackenedHRN.setValue(patientNode.map(node -> node.get("healthRecordNumber").asText()).orElse(""));
				blackenedHRN.setVisible(true);
			} else {
				blackenedHRN.setVisible(false);
			}
			phoneNumber.setVisible(pep.enforce(authentication, "read", "phoneNumber"));
			attendingDoctor.setVisible(pep.enforce(authentication, "read", "attendingDoctor"));
			attendingNurse.setVisible(pep.enforce(authentication, "read", "attendingNurse"));
			roomNumber.setVisible(pep.enforce(authentication, "readRoomNumber", patient));
		}
	}

	private static final Request buildRequest(Object subject, Object action, Object resource) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return new Request(mapper.valueToTree(subject), mapper.valueToTree(action), mapper.valueToTree(resource), null);
	}

	protected void updateButtonVisibility(boolean isNewPatient) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		saveBtn.setVisible(pep.enforce(authentication, isNewPatient ? "create" : "update", "profile"));
		deleteBtn.setVisible(!isNewPatient && pep.enforce(authentication, "delete", "profile"));
	}

	protected void onSave() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (pep.enforce(authentication, isNewPatient ? "create" : "update", "profile")) {

			// get the update authorization for each modified field to enforce obligation
			// handling
			// and reset the field if necessary
			if (!Objects.equals(name.getValue(), unmodifiedPatient.getName())) {
				if (!pep.enforce(authentication, "update", "name")) {
					patient.setName(unmodifiedPatient.getName());
				}
			}
			if (!Objects.equals(diagnosis.getValue(), unmodifiedPatient.getDiagnosis())) {
				if (!pep.enforce(authentication, "updateDiagnosis", patient)) {
					patient.setDiagnosis(unmodifiedPatient.getDiagnosis());
				}
			}
			if (!Objects.equals(healthRecordNumber.getValue(), unmodifiedPatient.getHealthRecordNumber())) {
				if (!pep.enforce(authentication, "update", "HRN")) {
					patient.setHealthRecordNumber(unmodifiedPatient.getHealthRecordNumber());
				}
			}
			if (!Objects.equals(phoneNumber.getValue(), unmodifiedPatient.getPhoneNumber())) {
				if (!pep.enforce(authentication, "update", "phoneNumber")) {
					patient.setPhoneNumber(unmodifiedPatient.getPhoneNumber());
				}
			}
			if (!Objects.equals(attendingDoctor.getValue(), unmodifiedPatient.getAttendingDoctor())) {
				if (!pep.enforce(authentication, "update", "attendingDoctor")) {
					patient.setAttendingDoctor(unmodifiedPatient.getAttendingDoctor());
				}
			}
			if (!Objects.equals(attendingNurse.getValue(), unmodifiedPatient.getAttendingNurse())) {
				if (!pep.enforce(authentication, "update", "attendingNurse")) {
					patient.setAttendingNurse(unmodifiedPatient.getAttendingNurse());
				}
			}
			if (!Objects.equals(roomNumber.getValue(), unmodifiedPatient.getRoomNumber())) {
				if (!pep.enforce(authentication, "update", "roomNumber")) {
					patient.setRoomNumber(unmodifiedPatient.getRoomNumber());
				}
			}

			patientRepo.save(patient);
			refreshCallback.refresh();
		} else {
			SecurityUtils.notifyNotAuthorized();
		}
		setVisible(false);
	}

	protected void onDelete() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (pep.enforce(authentication, "delete", "profile")) {
			patientRepo.delete(patient);
			refreshCallback.refresh();
		} else {
			SecurityUtils.notifyNotAuthorized();
		}
		setVisible(false);
	}
}
