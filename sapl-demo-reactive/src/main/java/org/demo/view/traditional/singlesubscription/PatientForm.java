package org.demo.view.traditional.singlesubscription;

import org.demo.service.UIController;
import org.demo.view.traditional.AbstractPatientForm;

/**
 * Concrete patient form implementation demonstrating the usage of SAPL single
 * subscriptions for controlling the visibility and enabling of form fields.
 */
class PatientForm extends AbstractPatientForm {

	private static final long serialVersionUID = 1L;

	PatientForm(UIController controller, RefreshCallback refreshCallback) {
		super(controller, refreshCallback);
	}

	@Override
	protected void updateFieldVisibility() {
		medicalRecordNumber.setVisible(isPermitted("read", medicalRecordNumber));
		name.setVisible(isPermitted("read", name));
		diagnosisText.setVisible(isPermitted("read", diagnosisText));
		icd11Code.setVisible(isPermitted("read", icd11Code));
		attendingDoctor.setVisible(isPermitted("read", attendingDoctor));
		attendingNurse.setVisible(isPermitted("read", attendingNurse));
		phoneNumber.setVisible(isPermitted("read", phoneNumber));
		roomNumber.setVisible(isPermitted("read", roomNumber, patient.getId()));
	}

	@Override
	protected void updateFieldEnabling() {
		medicalRecordNumber.setEnabled(isNewPatient() && isPermitted("edit", medicalRecordNumber));
		name.setEnabled(isPermitted("edit", name));
		icd11Code.setEnabled(isPermitted("edit", icd11Code));
		diagnosisText.setEnabled(isPermitted("edit", diagnosisText));
		attendingDoctor.setEnabled(isPermitted("edit", attendingDoctor));
		attendingNurse.setEnabled(isPermitted("edit", attendingNurse));
		phoneNumber.setEnabled(isPermitted("edit", phoneNumber));
		roomNumber.setEnabled(isPermitted("edit", roomNumber));
	}

}
