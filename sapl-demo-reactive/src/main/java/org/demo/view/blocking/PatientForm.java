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
		blackenedIcd11Code.setEnabled(false);

		if (isNewPatient) {
			mrn.setEnabled(true);
			name.setEnabled(true);
			icd11Code.setEnabled(true);
			diagnosisText.setEnabled(false); // only the admin can create a new patient, but doctors add a diagnosis
			attendingDoctor.setEnabled(true);
			attendingNurse.setEnabled(true);
			phoneNumber.setEnabled(true);
			roomNumber.setEnabled(true);
		} else {
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			mrn.setEnabled(pep.enforce(authentication, "update", "mrn"));
			name.setEnabled(pep.enforce(authentication, "update", "name"));
			icd11Code.setEnabled(pep.enforce(authentication, "update", "icd11"));
			diagnosisText.setEnabled(pep.enforce(authentication, "updateDiagnosis", patient));
			attendingDoctor.setEnabled(pep.enforce(authentication, "update", "attendingDoctor"));
			attendingNurse.setEnabled(pep.enforce(authentication, "update", "attendingNurse"));
			phoneNumber.setEnabled(pep.enforce(authentication, "update", "phoneNumber"));
			roomNumber.setEnabled(pep.enforce(authentication, "update", "roomNumber"));
		}
	}

	protected void updateFieldVisibility(boolean isNewPatient) {
		if (isNewPatient) {
			id.setVisible(false);
			mrn.setVisible(true);
			name.setVisible(true);
			icd11Code.setVisible(true);
			blackenedIcd11Code.setVisible(false);
			diagnosisText.setVisible(false); // only the admin can create a new patient, but doctors add a diagnosis
			attendingDoctor.setVisible(true);
			attendingNurse.setVisible(true);
			phoneNumber.setVisible(true);
			roomNumber.setVisible(true);
		} else {
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			id.setVisible(true);
			mrn.setVisible(pep.enforce(authentication, "read", "mrn"));
			name.setVisible(pep.enforce(authentication, "read", "name"));
			diagnosisText.setVisible(pep.enforce(authentication, "read", "diagnosis"));
			icd11Code.setVisible(pep.enforce(authentication, "read", "icd11"));
			final Response readBlackenedIcd11 = pdp.decide(buildRequest(authentication, "readBlackenedIcd11", patient))
					.blockFirst();
			if (readBlackenedIcd11.getDecision() == Decision.PERMIT) {
				final Optional<JsonNode> patientNode = readBlackenedIcd11.getResource();
				blackenedIcd11Code.setValue(patientNode.map(node -> node.get("icd11Code").asText()).orElse(""));
				blackenedIcd11Code.setVisible(true);
			} else {
				blackenedIcd11Code.setVisible(false);
			}
			attendingDoctor.setVisible(pep.enforce(authentication, "read", "attendingDoctor"));
			attendingNurse.setVisible(pep.enforce(authentication, "read", "attendingNurse"));
			phoneNumber.setVisible(pep.enforce(authentication, "read", "phoneNumber"));
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
			if (!Objects.equals(mrn.getValue(), unmodifiedPatient.getMedicalRecordNumber())) {
				if (!pep.enforce(authentication, "update", "mrn")) {
					patient.setName(unmodifiedPatient.getMedicalRecordNumber());
				}
			}
			if (!Objects.equals(name.getValue(), unmodifiedPatient.getName())) {
				if (!pep.enforce(authentication, "update", "name")) {
					patient.setName(unmodifiedPatient.getName());
				}
			}
			if (!Objects.equals(icd11Code.getValue(), unmodifiedPatient.getIcd11Code())) {
				if (!pep.enforce(authentication, "update", "icd11")) {
					patient.setIcd11Code(unmodifiedPatient.getIcd11Code());
				}
			}
			if (!Objects.equals(diagnosisText.getValue(), unmodifiedPatient.getDiagnosisText())) {
				if (!pep.enforce(authentication, "updateDiagnosis", patient)) {
					patient.setDiagnosisText(unmodifiedPatient.getDiagnosisText());
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
			if (!Objects.equals(phoneNumber.getValue(), unmodifiedPatient.getPhoneNumber())) {
				if (!pep.enforce(authentication, "update", "phoneNumber")) {
					patient.setPhoneNumber(unmodifiedPatient.getPhoneNumber());
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
			patientRepo.deleteById(patient.getId());
			refreshCallback.refresh();
		} else {
			SecurityUtils.notifyNotAuthorized();
		}
		setVisible(false);
	}
}
