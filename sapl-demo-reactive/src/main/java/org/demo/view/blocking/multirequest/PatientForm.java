package org.demo.view.blocking.multirequest;

import static io.sapl.api.pdp.multirequest.IdentifiableAction.READ_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableSubject.AUTHENTICATION_ID;

import java.io.IOException;

import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.demo.view.blocking.AbstractPatientForm;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pdp.multirequest.IdentifiableAction;
import io.sapl.api.pdp.multirequest.IdentifiableResource;
import io.sapl.api.pdp.multirequest.IdentifiableSubject;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.api.pdp.multirequest.RequestElements;
import io.sapl.spring.PolicyEnforcementPoint;

class PatientForm extends AbstractPatientForm {

	private final PolicyEnforcementPoint pep;

	PatientForm(PolicyEnforcementPoint pep, UIController controller, RefreshCallback refreshCallback) {
		super(controller, refreshCallback);
		this.pep = pep;
	}

	protected void updateFieldVisibility() {
		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, SecurityUtils.getAuthentication()));
		multiRequest.addAction(new IdentifiableAction(READ_ID, "read"));
		multiRequest.addResource((String) medicalRecordNumber.getData());
		multiRequest.addResource((String) name.getData());
		multiRequest.addResource((String) diagnosisText.getData());
		multiRequest.addResource((String) icd11Code.getData());
		multiRequest.addResource((String) attendingDoctor.getData());
		multiRequest.addResource((String) attendingNurse.getData());
		multiRequest.addResource((String) phoneNumber.getData());
		try {
			final JsonNode resource = objectMapper.readTree("{ \"id\": " + patient.getId() + ", \"uiElement\": \"" + roomNumber.getData() + "\"}");
			multiRequest.addResource(new IdentifiableResource("roomNumber", resource));
		} catch (IOException e) {
			multiRequest.addResource(new IdentifiableResource("roomNumber", roomNumber.getData()));
		}
		multiRequest.addRequest("readMrn", new RequestElements(AUTHENTICATION_ID, READ_ID, (String) medicalRecordNumber.getData()));
		multiRequest.addRequest("readName", new RequestElements(AUTHENTICATION_ID, READ_ID, (String) name.getData()));
		multiRequest.addRequest("readDiagnosis", new RequestElements(AUTHENTICATION_ID, READ_ID, (String) diagnosisText.getData()));
		multiRequest.addRequest("readIcd11", new RequestElements(AUTHENTICATION_ID, READ_ID, (String) icd11Code.getData()));
		multiRequest.addRequest("readAttendingDoctor", new RequestElements(AUTHENTICATION_ID, READ_ID, (String) attendingDoctor.getData()));
		multiRequest.addRequest("readAttendingNurse", new RequestElements(AUTHENTICATION_ID, READ_ID, (String) attendingNurse.getData()));
		multiRequest.addRequest("readPhoneNumber", new RequestElements(AUTHENTICATION_ID, READ_ID, (String) phoneNumber.getData()));
		multiRequest.addRequest("readRoomNumber", new RequestElements(AUTHENTICATION_ID, READ_ID, "roomNumber"));

		final MultiResponse multiResponse = pep.filterEnforce(multiRequest).blockFirst();

		medicalRecordNumber.setVisible(multiResponse.isAccessPermittedForRequestWithId("readMrn"));
		name.setVisible(multiResponse.isAccessPermittedForRequestWithId("readName"));
		diagnosisText.setVisible(multiResponse.isAccessPermittedForRequestWithId("readDiagnosis"));
		icd11Code.setVisible(multiResponse.isAccessPermittedForRequestWithId("readIcd11"));
		attendingDoctor.setVisible(multiResponse.isAccessPermittedForRequestWithId("readAttendingDoctor"));
		attendingNurse.setVisible(multiResponse.isAccessPermittedForRequestWithId("readAttendingNurse"));
		phoneNumber.setVisible(multiResponse.isAccessPermittedForRequestWithId("readPhoneNumber"));
		roomNumber.setVisible(multiResponse.isAccessPermittedForRequestWithId("readRoomNumber"));
	}

	protected void updateFieldEnabling() {
		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, SecurityUtils.getAuthentication()));
		multiRequest.addAction("edit");
		multiRequest.addResource((String) medicalRecordNumber.getData());
		multiRequest.addResource((String) name.getData());
		multiRequest.addResource((String) diagnosisText.getData());
		multiRequest.addResource((String) icd11Code.getData());
		multiRequest.addResource((String) attendingDoctor.getData());
		multiRequest.addResource((String) attendingNurse.getData());
		multiRequest.addResource((String) phoneNumber.getData());
		multiRequest.addResource((String) roomNumber.getData());
		multiRequest.addRequest("editMrn", new RequestElements(AUTHENTICATION_ID, "edit", (String) medicalRecordNumber.getData()));
		multiRequest.addRequest("editName", new RequestElements(AUTHENTICATION_ID, "edit", (String) name.getData()));
		multiRequest.addRequest("editDiagnosis", new RequestElements(AUTHENTICATION_ID, "edit", (String) diagnosisText.getData()));
		multiRequest.addRequest("editIcd11", new RequestElements(AUTHENTICATION_ID, "edit", (String) icd11Code.getData()));
		multiRequest.addRequest("editAttendingDoctor", new RequestElements(AUTHENTICATION_ID, "edit", (String) attendingDoctor.getData()));
		multiRequest.addRequest("editAttendingNurse", new RequestElements(AUTHENTICATION_ID, "edit", (String) attendingNurse.getData()));
		multiRequest.addRequest("editPhoneNumber", new RequestElements(AUTHENTICATION_ID, "edit", (String) phoneNumber.getData()));
		multiRequest.addRequest("editRoomNumber", new RequestElements(AUTHENTICATION_ID, "edit", (String) roomNumber.getData()));

		final MultiResponse multiResponse = pep.filterEnforce(multiRequest).blockFirst();

		boolean isNewPatient = patient.getId() == null;
		medicalRecordNumber.setEnabled(isNewPatient && multiResponse.isAccessPermittedForRequestWithId("editMrn"));
		name.setEnabled(multiResponse.isAccessPermittedForRequestWithId("editName"));
		icd11Code.setEnabled(multiResponse.isAccessPermittedForRequestWithId("editIcd11"));
		diagnosisText.setEnabled(multiResponse.isAccessPermittedForRequestWithId("editDiagnosis"));
		attendingDoctor.setEnabled(multiResponse.isAccessPermittedForRequestWithId("editAttendingDoctor"));
		attendingNurse.setEnabled(multiResponse.isAccessPermittedForRequestWithId("editAttendingNurse"));
		phoneNumber.setEnabled(multiResponse.isAccessPermittedForRequestWithId("editPhoneNumber"));
		roomNumber.setEnabled(multiResponse.isAccessPermittedForRequestWithId("editRoomNumber"));
	}

	protected void updateButtonVisibility() {
		boolean isNewPatient = patient.getId() == null;

		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, SecurityUtils.getAuthentication()));
		multiRequest.addAction("useForCreate");
		multiRequest.addAction("useForUpdate");
		multiRequest.addAction("use");
		multiRequest.addResource((String) saveBtn.getData());
		try {
			final JsonNode resource = objectMapper.readTree("{ \"id\": " + patient.getId() + ", \"uiElement\": \"" + deleteBtn.getData() + "\"}");
			multiRequest.addResource(new IdentifiableResource("deleteBtn", resource));
		} catch (IOException e) {
			multiRequest.addResource(new IdentifiableResource("deleteBtn", deleteBtn.getData()));
		}
		multiRequest.addRequest("useSaveBtn", new RequestElements(AUTHENTICATION_ID, isNewPatient ? "useForCreate" : "useForUpdate", (String) saveBtn.getData()));
		multiRequest.addRequest("useDeleteBtn", new RequestElements(AUTHENTICATION_ID, "use", "deleteBtn"));

		final MultiResponse multiResponse = pep.filterEnforce(multiRequest).blockFirst();

		saveBtn.setVisible(multiResponse.isAccessPermittedForRequestWithId("useSaveBtn"));
		deleteBtn.setVisible(!isNewPatient && multiResponse.isAccessPermittedForRequestWithId("useDeleteBtn"));
	}
}
