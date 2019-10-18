package org.demo.view.traditional.multisubscription;

import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.demo.view.traditional.AbstractPatientForm;
import org.springframework.security.core.Authentication;

import io.sapl.api.pdp.multisubscription.MultiAuthSubscription;

/**
 * Concrete patient form implementation demonstrating the usage of SAPL multi-subscriptions for
 * controlling the visibility and enabling of form fields.
 */
class PatientForm extends AbstractPatientForm {

	private static final long serialVersionUID = 1L;

	PatientForm(UIController controller, RefreshCallback refreshCallback) {
		super(controller, refreshCallback);
	}

	@Override
	protected void updateFieldVisibility() {
		final MultiSubscriptionStreamManager streamManager = getSession().getAttribute(MultiSubscriptionStreamManager.class);
		if (!streamManager.hasSubscriptionFor("fieldVisibility")) {
			streamManager.setupNewMultiSubscription("fieldVisibility", createMultiSubscriptionForFieldVisibility());
		}
		medicalRecordNumber.setVisible(streamManager.isAccessPermittedForAuthSubscriptionWithId("readMrn"));
		name.setVisible(streamManager.isAccessPermittedForAuthSubscriptionWithId("readName"));
		diagnosisText.setVisible(streamManager.isAccessPermittedForAuthSubscriptionWithId("readDiagnosis"));
		icd11Code.setVisible(streamManager.isAccessPermittedForAuthSubscriptionWithId("readIcd11"));
		attendingDoctor.setVisible(streamManager.isAccessPermittedForAuthSubscriptionWithId("readAttendingDoctor"));
		attendingNurse.setVisible(streamManager.isAccessPermittedForAuthSubscriptionWithId("readAttendingNurse"));
		phoneNumber.setVisible(streamManager.isAccessPermittedForAuthSubscriptionWithId("readPhoneNumber"));

		roomNumber.setVisible(isPermitted("read", roomNumber, patient.getId()));
	}

	private MultiAuthSubscription createMultiSubscriptionForFieldVisibility() {
		final Authentication authentication = SecurityUtils.getAuthentication();
		return new MultiAuthSubscription().addAuthSubscription("readMrn", authentication, "read", medicalRecordNumber.getData())
				.addAuthSubscription("readName", authentication, "read", name.getData())
				.addAuthSubscription("readDiagnosis", authentication, "read", diagnosisText.getData())
				.addAuthSubscription("readIcd11", authentication, "read", icd11Code.getData())
				.addAuthSubscription("readAttendingDoctor", authentication, "read", attendingDoctor.getData())
				.addAuthSubscription("readAttendingNurse", authentication, "read", attendingNurse.getData())
				.addAuthSubscription("readPhoneNumber", authentication, "read", phoneNumber.getData());
	}

	@Override
	protected void updateFieldEnabling() {
		final MultiSubscriptionStreamManager streamManager = getSession().getAttribute(MultiSubscriptionStreamManager.class);
		if (!streamManager.hasSubscriptionFor("fieldEnabling")) {
			streamManager.setupNewMultiSubscription("fieldEnabling", createMultiSubscriptionForFieldEnabling());
		}
		medicalRecordNumber.setEnabled(isNewPatient() && streamManager.isAccessPermittedForAuthSubscriptionWithId("editMrn"));
		name.setEnabled(streamManager.isAccessPermittedForAuthSubscriptionWithId("editName"));
		icd11Code.setEnabled(streamManager.isAccessPermittedForAuthSubscriptionWithId("editIcd11"));
		diagnosisText.setEnabled(streamManager.isAccessPermittedForAuthSubscriptionWithId("editDiagnosis"));
		attendingDoctor.setEnabled(streamManager.isAccessPermittedForAuthSubscriptionWithId("editAttendingDoctor"));
		attendingNurse.setEnabled(streamManager.isAccessPermittedForAuthSubscriptionWithId("editAttendingNurse"));
		phoneNumber.setEnabled(streamManager.isAccessPermittedForAuthSubscriptionWithId("editPhoneNumber"));
		roomNumber.setEnabled(streamManager.isAccessPermittedForAuthSubscriptionWithId("editRoomNumber"));
	}

	private MultiAuthSubscription createMultiSubscriptionForFieldEnabling() {
		final Authentication authentication = SecurityUtils.getAuthentication();
		return new MultiAuthSubscription().addAuthSubscription("editMrn", authentication, "edit", medicalRecordNumber.getData())
				.addAuthSubscription("editName", authentication, "edit", name.getData())
				.addAuthSubscription("editDiagnosis", authentication, "edit", diagnosisText.getData())
				.addAuthSubscription("editIcd11", authentication, "edit", icd11Code.getData())
				.addAuthSubscription("editAttendingDoctor", authentication, "edit", attendingDoctor.getData())
				.addAuthSubscription("editAttendingNurse", authentication, "edit", attendingNurse.getData())
				.addAuthSubscription("editPhoneNumber", authentication, "edit", phoneNumber.getData())
				.addAuthSubscription("editRoomNumber", authentication, "edit", roomNumber.getData());
	}

}
