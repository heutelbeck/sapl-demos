package org.demo.view.traditional.multisubscription;

import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.demo.view.traditional.AbstractPatientForm;
import org.springframework.security.core.Authentication;

import io.sapl.api.pdp.MultiAuthorizationSubscription;

/**
 * Concrete patient form implementation demonstrating the usage of SAPL
 * multi-subscriptions for controlling the visibility and enabling of form fields.
 */
class PatientForm extends AbstractPatientForm {

	private static final long serialVersionUID = 1L;

	PatientForm(UIController controller, RefreshCallback refreshCallback) {
		super(controller, refreshCallback);
	}

	@Override
	protected void updateFieldVisibility() {
		final MultiSubscriptionStreamManager streamManager = getSession()
				.getAttribute(MultiSubscriptionStreamManager.class);
		if (!streamManager.hasSubscriptionFor("fieldVisibility")) {
			streamManager.setupNewMultiSubscription("fieldVisibility", createMultiSubscriptionForFieldVisibility());
		}
		medicalRecordNumber.setVisible(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("readMrn"));
		name.setVisible(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("readName"));
		diagnosisText.setVisible(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("readDiagnosis"));
		icd11Code.setVisible(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("readIcd11"));
		attendingDoctor
				.setVisible(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("readAttendingDoctor"));
		attendingNurse
				.setVisible(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("readAttendingNurse"));
		phoneNumber.setVisible(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("readPhoneNumber"));

		roomNumber.setVisible(isPermitted("read", roomNumber, patient.getId()));
	}

	private MultiAuthorizationSubscription createMultiSubscriptionForFieldVisibility() {
		final Authentication authentication = SecurityUtils.getAuthentication();
		return new MultiAuthorizationSubscription()
				.addAuthorizationSubscription("readMrn", authentication, "read", medicalRecordNumber.getData())
				.addAuthorizationSubscription("readName", authentication, "read", name.getData())
				.addAuthorizationSubscription("readDiagnosis", authentication, "read", diagnosisText.getData())
				.addAuthorizationSubscription("readIcd11", authentication, "read", icd11Code.getData())
				.addAuthorizationSubscription("readAttendingDoctor", authentication, "read", attendingDoctor.getData())
				.addAuthorizationSubscription("readAttendingNurse", authentication, "read", attendingNurse.getData())
				.addAuthorizationSubscription("readPhoneNumber", authentication, "read", phoneNumber.getData());
	}

	@Override
	protected void updateFieldEnabling() {
		final MultiSubscriptionStreamManager streamManager = getSession()
				.getAttribute(MultiSubscriptionStreamManager.class);
		if (!streamManager.hasSubscriptionFor("fieldEnabling")) {
			streamManager.setupNewMultiSubscription("fieldEnabling", createMultiSubscriptionForFieldEnabling());
		}
		medicalRecordNumber.setEnabled(
				isNewPatient() && streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editMrn"));
		name.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editName"));
		icd11Code.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editIcd11"));
		diagnosisText.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editDiagnosis"));
		attendingDoctor
				.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editAttendingDoctor"));
		attendingNurse
				.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editAttendingNurse"));
		phoneNumber.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editPhoneNumber"));
		roomNumber.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("editRoomNumber"));
	}

	private MultiAuthorizationSubscription createMultiSubscriptionForFieldEnabling() {
		final Authentication authentication = SecurityUtils.getAuthentication();
		return new MultiAuthorizationSubscription()
				.addAuthorizationSubscription("editMrn", authentication, "edit", medicalRecordNumber.getData())
				.addAuthorizationSubscription("editName", authentication, "edit", name.getData())
				.addAuthorizationSubscription("editDiagnosis", authentication, "edit", diagnosisText.getData())
				.addAuthorizationSubscription("editIcd11", authentication, "edit", icd11Code.getData())
				.addAuthorizationSubscription("editAttendingDoctor", authentication, "edit", attendingDoctor.getData())
				.addAuthorizationSubscription("editAttendingNurse", authentication, "edit", attendingNurse.getData())
				.addAuthorizationSubscription("editPhoneNumber", authentication, "edit", phoneNumber.getData())
				.addAuthorizationSubscription("editRoomNumber", authentication, "edit", roomNumber.getData());
	}

}
