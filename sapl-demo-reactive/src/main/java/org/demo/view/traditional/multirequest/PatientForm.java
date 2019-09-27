package org.demo.view.traditional.multirequest;

import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.demo.view.traditional.AbstractPatientForm;
import org.springframework.security.core.Authentication;

import io.sapl.api.pdp.multirequest.MultiRequest;

/**
 * Concrete patient form implementation demonstrating the usage of SAPL multi-requests for
 * controlling the visibility and enabling of form fields.
 */
class PatientForm extends AbstractPatientForm {

	private static final long serialVersionUID = 1L;

	PatientForm(UIController controller, RefreshCallback refreshCallback) {
		super(controller, refreshCallback);
	}

	@Override
	protected void updateFieldVisibility() {
		final MultiRequestStreamManager streamManager = getSession().getAttribute(MultiRequestStreamManager.class);
		if (!streamManager.hasMultiRequestSubscriptionFor("fieldVisibility")) {
			streamManager.setupNewMultiRequest("fieldVisibility", createMultiRequestForFieldVisibility());
		}
		medicalRecordNumber.setVisible(streamManager.isAccessPermittedForRequestWithId("readMrn"));
		name.setVisible(streamManager.isAccessPermittedForRequestWithId("readName"));
		diagnosisText.setVisible(streamManager.isAccessPermittedForRequestWithId("readDiagnosis"));
		icd11Code.setVisible(streamManager.isAccessPermittedForRequestWithId("readIcd11"));
		attendingDoctor.setVisible(streamManager.isAccessPermittedForRequestWithId("readAttendingDoctor"));
		attendingNurse.setVisible(streamManager.isAccessPermittedForRequestWithId("readAttendingNurse"));
		phoneNumber.setVisible(streamManager.isAccessPermittedForRequestWithId("readPhoneNumber"));

		roomNumber.setVisible(isPermitted("read", roomNumber, patient.getId()));
	}

	private MultiRequest createMultiRequestForFieldVisibility() {
		final Authentication authentication = SecurityUtils.getAuthentication();
		return new MultiRequest().addRequest("readMrn", authentication, "read", medicalRecordNumber.getData())
				.addRequest("readName", authentication, "read", name.getData())
				.addRequest("readDiagnosis", authentication, "read", diagnosisText.getData())
				.addRequest("readIcd11", authentication, "read", icd11Code.getData())
				.addRequest("readAttendingDoctor", authentication, "read", attendingDoctor.getData())
				.addRequest("readAttendingNurse", authentication, "read", attendingNurse.getData())
				.addRequest("readPhoneNumber", authentication, "read", phoneNumber.getData());
	}

	@Override
	protected void updateFieldEnabling() {
		final MultiRequestStreamManager streamManager = getSession().getAttribute(MultiRequestStreamManager.class);
		if (!streamManager.hasMultiRequestSubscriptionFor("fieldEnabling")) {
			streamManager.setupNewMultiRequest("fieldEnabling", createMultiRequestForFieldEnabling());
		}
		medicalRecordNumber.setEnabled(isNewPatient() && streamManager.isAccessPermittedForRequestWithId("editMrn"));
		name.setEnabled(streamManager.isAccessPermittedForRequestWithId("editName"));
		icd11Code.setEnabled(streamManager.isAccessPermittedForRequestWithId("editIcd11"));
		diagnosisText.setEnabled(streamManager.isAccessPermittedForRequestWithId("editDiagnosis"));
		attendingDoctor.setEnabled(streamManager.isAccessPermittedForRequestWithId("editAttendingDoctor"));
		attendingNurse.setEnabled(streamManager.isAccessPermittedForRequestWithId("editAttendingNurse"));
		phoneNumber.setEnabled(streamManager.isAccessPermittedForRequestWithId("editPhoneNumber"));
		roomNumber.setEnabled(streamManager.isAccessPermittedForRequestWithId("editRoomNumber"));
	}

	private MultiRequest createMultiRequestForFieldEnabling() {
		final Authentication authentication = SecurityUtils.getAuthentication();
		return new MultiRequest().addRequest("editMrn", authentication, "edit", medicalRecordNumber.getData())
				.addRequest("editName", authentication, "edit", name.getData())
				.addRequest("editDiagnosis", authentication, "edit", diagnosisText.getData())
				.addRequest("editIcd11", authentication, "edit", icd11Code.getData())
				.addRequest("editAttendingDoctor", authentication, "edit", attendingDoctor.getData())
				.addRequest("editAttendingNurse", authentication, "edit", attendingNurse.getData())
				.addRequest("editPhoneNumber", authentication, "edit", phoneNumber.getData())
				.addRequest("editRoomNumber", authentication, "edit", roomNumber.getData());
	}

}
