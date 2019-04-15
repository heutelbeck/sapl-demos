package org.demo.view.blocking.multirequest;

import static io.sapl.api.pdp.multirequest.IdentifiableAction.CREATE_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableAction.DELETE_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableAction.READ_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableAction.UPDATE_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableSubject.AUTHENTICATION_ID;

import java.util.Objects;
import java.util.Optional;

import org.demo.domain.PatientRepository;
import org.demo.security.SecurityUtils;
import org.demo.view.blocking.AbstractPatientForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.api.pdp.multirequest.IdentifiableAction;
import io.sapl.api.pdp.multirequest.IdentifiableResource;
import io.sapl.api.pdp.multirequest.IdentifiableSubject;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.api.pdp.multirequest.RequestElements;
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

			final MultiRequest multiRequest = new MultiRequest();
			multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
			multiRequest.addAction(UPDATE_ID);
			multiRequest.addAction("updateDiagnosis");
			multiRequest.addResource("mrn");
			multiRequest.addResource("name");
			multiRequest.addResource(new IdentifiableResource("patient", patient));
			multiRequest.addResource("idc11");
			multiRequest.addResource("attendingDoctor");
			multiRequest.addResource("attendingNurse");
			multiRequest.addResource("phoneNumber");
			multiRequest.addResource("roomNumber");
			multiRequest.addRequest("updateMrn", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "mrn"));
			multiRequest.addRequest("updateName", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "name"));
			multiRequest.addRequest("updateDiagnosis",
					new RequestElements(AUTHENTICATION_ID, "updateDiagnosis", "patient"));
			multiRequest.addRequest("updateIcd11", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "icd11"));
			multiRequest.addRequest("updateAttendingDoctor",
					new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "attendingDoctor"));
			multiRequest.addRequest("updateAttendingNurse",
					new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "attendingNurse"));
			multiRequest.addRequest("updatePhoneNumber",
					new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "phoneNumber"));
			multiRequest.addRequest("updateRoomNumber",
					new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "roomNumber"));

			final MultiResponse multiResponse = pdp.decideAll(multiRequest).blockFirst();

			mrn.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updateMrn"));
			name.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updateName"));
			icd11Code.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updateIcd11"));
			diagnosisText.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updateDiagnosis"));
			attendingDoctor.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updateAttendingDoctor"));
			attendingNurse.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updateAttendingNurse"));
			phoneNumber.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updatePhoneNumber"));
			roomNumber.setEnabled(multiResponse.isAccessPermittedForRequestWithId("updateRoomNumber"));
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

			final MultiRequest multiRequest = new MultiRequest();
			multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
			multiRequest.addAction(new IdentifiableAction(READ_ID, "read"));
			multiRequest.addAction("readBlackenedIcd11");
			multiRequest.addAction("readRoomNumber");
			multiRequest.addResource("mrn");
			multiRequest.addResource("name");
			multiRequest.addResource("icd11");
			multiRequest.addResource("diagnosis");
			multiRequest.addResource(new IdentifiableResource("patient", patient));
			multiRequest.addResource("attendingDoctor");
			multiRequest.addResource("attendingNurse");
			multiRequest.addResource("phoneNumber");
			multiRequest.addResource("roomNumber");
			multiRequest.addRequest("readMrn", new RequestElements(AUTHENTICATION_ID, READ_ID, "mrn"));
			multiRequest.addRequest("readName", new RequestElements(AUTHENTICATION_ID, READ_ID, "name"));
			multiRequest.addRequest("readIcd11", new RequestElements(AUTHENTICATION_ID, READ_ID, "icd11"));
			multiRequest.addRequest("readBlackenedIcd11",
					new RequestElements(AUTHENTICATION_ID, "readBlackenedIcd11", "patient"));
			multiRequest.addRequest("readDiagnosis", new RequestElements(AUTHENTICATION_ID, READ_ID, "diagnosis"));
			multiRequest.addRequest("readAttendingDoctor",
					new RequestElements(AUTHENTICATION_ID, READ_ID, "attendingDoctor"));
			multiRequest.addRequest("readAttendingNurse",
					new RequestElements(AUTHENTICATION_ID, READ_ID, "attendingNurse"));
			multiRequest.addRequest("readPhoneNumber", new RequestElements(AUTHENTICATION_ID, READ_ID, "phoneNumber"));
			multiRequest.addRequest("readRoomNumber",
					new RequestElements(AUTHENTICATION_ID, "readRoomNumber", "patient"));

			final MultiResponse multiResponse = pdp.decideAll(multiRequest).blockFirst();

			id.setVisible(true);
			mrn.setVisible(multiResponse.isAccessPermittedForRequestWithId("readMrn"));
			name.setVisible(multiResponse.isAccessPermittedForRequestWithId("readName"));
			icd11Code.setVisible(multiResponse.isAccessPermittedForRequestWithId("readIcd11"));
			diagnosisText.setVisible(multiResponse.isAccessPermittedForRequestWithId("readDiagnosis"));
			final Response readBlackenedIcd11 = multiResponse.getResponseForRequestWithId("readBlackenedIcd11");
			if (readBlackenedIcd11.getDecision() == Decision.PERMIT) {
				final Optional<JsonNode> patientNode = readBlackenedIcd11.getResource();
				blackenedIcd11Code.setValue(patientNode.map(node -> node.get("icd11Code").asText()).orElse(""));
				blackenedIcd11Code.setVisible(true);
			} else {
				blackenedIcd11Code.setVisible(false);
			}
			attendingDoctor.setVisible(multiResponse.isAccessPermittedForRequestWithId("readAttendingDoctor"));
			attendingNurse.setVisible(multiResponse.isAccessPermittedForRequestWithId("readAttendingNurse"));
			phoneNumber.setVisible(multiResponse.isAccessPermittedForRequestWithId("readPhoneNumber"));
			roomNumber.setVisible(multiResponse.isAccessPermittedForRequestWithId("readRoomNumber"));
		}
	}

	private static final Request buildRequest(Object subject, Object action, Object resource) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return new Request(mapper.valueToTree(subject), mapper.valueToTree(action), mapper.valueToTree(resource), null);
	}

	protected void updateButtonVisibility(boolean isNewPatient) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
		multiRequest.addAction(new IdentifiableAction(UPDATE_ID, "update"));
		multiRequest.addAction(new IdentifiableAction(DELETE_ID, "delete"));
		multiRequest.addResource("profile");
		if (isNewPatient) {
			multiRequest.addAction(new IdentifiableAction(CREATE_ID, "create"));
			multiRequest.addRequest("saveProfile", new RequestElements(AUTHENTICATION_ID, CREATE_ID, "profile"));
		} else {
			multiRequest.addAction(new IdentifiableAction(UPDATE_ID, "update"));
			multiRequest.addRequest("saveProfile", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "profile"));
		}
		multiRequest.addRequest("deleteProfile", new RequestElements(AUTHENTICATION_ID, DELETE_ID, "profile"));

		final MultiResponse multiResponse = pdp.decideAll(multiRequest).blockFirst();

		saveBtn.setVisible(multiResponse.isAccessPermittedForRequestWithId("saveProfile"));
		deleteBtn.setVisible(!isNewPatient && multiResponse.isAccessPermittedForRequestWithId("deleteProfile"));
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
