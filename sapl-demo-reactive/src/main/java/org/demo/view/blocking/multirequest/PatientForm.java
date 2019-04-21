package org.demo.view.blocking.multirequest;

import java.io.IOException;

import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.demo.view.blocking.AbstractPatientForm;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.spring.PolicyEnforcementPoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PatientForm extends AbstractPatientForm {

	private final PolicyEnforcementPoint pep;

	PatientForm(PolicyEnforcementPoint pep, UIController controller, RefreshCallback refreshCallback) {
		super(controller, refreshCallback);
		this.pep = pep;
	}

	protected void updateFieldVisibility() {
		final Authentication authentication = SecurityUtils.getAuthentication();

		final MultiRequest multiRequest = new MultiRequest()
				.addRequest("readMrn", authentication, "read", medicalRecordNumber.getData())
				.addRequest("readName", authentication, "read", name.getData())
				.addRequest("readDiagnosis", authentication, "read", diagnosisText.getData())
				.addRequest("readIcd11", authentication, "read", icd11Code.getData())
				.addRequest("readAttendingDoctor", authentication, "read", attendingDoctor.getData())
				.addRequest("readAttendingNurse", authentication, "read", attendingNurse.getData())
				.addRequest("readPhoneNumber", authentication, "read", phoneNumber.getData());
		try {
			final JsonNode resource = objectMapper.readTree("{ \"id\": " + patient.getId() + ", \"uiElement\": \"" + roomNumber.getData() + "\"}");
			multiRequest.addRequest("readRoomNumber", authentication, "read", resource);
		} catch (IOException e) {
			LOGGER.error("Error while creating a JsonNode from a JSON string.", e);
		}

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
		final Authentication authentication = SecurityUtils.getAuthentication();
		final MultiRequest multiRequest = new MultiRequest()
				.addRequest("editMrn", authentication, "edit", medicalRecordNumber.getData())
				.addRequest("editName", authentication, "edit", name.getData())
				.addRequest("editDiagnosis", authentication, "edit", diagnosisText.getData())
				.addRequest("editIcd11", authentication, "edit", icd11Code.getData())
				.addRequest("editAttendingDoctor", authentication, "edit", attendingDoctor.getData())
				.addRequest("editAttendingNurse", authentication, "edit", attendingNurse.getData())
				.addRequest("editPhoneNumber", authentication, "edit", phoneNumber.getData())
				.addRequest("editRoomNumber", authentication, "edit", roomNumber.getData());

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

		final Authentication authentication = SecurityUtils.getAuthentication();
		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addRequest("useSaveBtn", authentication, isNewPatient ? "useForCreate" : "useForUpdate", saveBtn.getData());
		try {
			final JsonNode resource = objectMapper.readTree("{ \"id\": " + patient.getId() + ", \"uiElement\": \"" + deleteBtn.getData() + "\"}");
			multiRequest.addRequest("useDeleteBtn", authentication, "use", resource);
		} catch (IOException e) {
			LOGGER.error("Error while creating a JsonNode from a JSON string.", e);
		}

		final MultiResponse multiResponse = pep.filterEnforce(multiRequest).blockFirst();

		saveBtn.setVisible(multiResponse.isAccessPermittedForRequestWithId("useSaveBtn"));
		deleteBtn.setVisible(!isNewPatient && multiResponse.isAccessPermittedForRequestWithId("useDeleteBtn"));
	}
}
