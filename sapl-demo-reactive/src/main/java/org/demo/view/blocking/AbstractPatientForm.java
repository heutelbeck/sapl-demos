package org.demo.view.blocking;

import java.util.Objects;

import org.demo.domain.Patient;
import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public abstract class AbstractPatientForm extends FormLayout {

	protected TextField medicalRecordNumber;

	protected TextField name;

	protected TextField icd11Code;

	protected TextField diagnosisText;

	protected TextField attendingDoctor;

	protected TextField attendingNurse;

	protected TextField phoneNumber;

	protected TextField roomNumber;

	private Binder<Patient> binder;

	protected Button saveBtn;

	protected Button deleteBtn;

	private UIController controller;

	private RefreshCallback refreshCallback;

	protected ObjectMapper objectMapper;

	protected Patient patient;

	private Patient unmodifiedPatient;

	protected AbstractPatientForm(UIController controller,
			RefreshCallback refreshCallback) {
		this.controller = controller;
		this.refreshCallback = refreshCallback;

		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new Jdk8Module());

		setSizeFull();
		setVisible(false);

		addFormFields();
		bindFormFields();
		addButtonBar();
	}

	private void addFormFields() {
		medicalRecordNumber = new TextField("Medical Record Number");
		medicalRecordNumber.setData("ui:view:patients:mrnField");

		name = new TextField("Name");
		name.setData("ui:view:patients:nameField");

		icd11Code = new TextField("ICD-11");
		icd11Code.setData("ui:view:patients:icd11Field");

		diagnosisText = new TextField("Diagnosis");
		diagnosisText.setData("ui:view:patients:diagnosisField");

		attendingDoctor = new TextField("Attending Doctor");
		attendingDoctor.setData("ui:view:patients:doctorField");

		attendingNurse = new TextField("Attending Nurse");
		attendingNurse.setData("ui:view:patients:nurseField");

		phoneNumber = new TextField("Phone");
		phoneNumber.setData("ui:view:patients:phoneField");

		roomNumber = new TextField("Room Number");
		roomNumber.setData("ui:view:patients:roomField");

		medicalRecordNumber.setSizeFull();
		name.setSizeFull();
		icd11Code.setSizeFull();
		diagnosisText.setSizeFull();
		attendingDoctor.setSizeFull();
		attendingNurse.setSizeFull();
		phoneNumber.setSizeFull();
		roomNumber.setSizeFull();

		addComponents(medicalRecordNumber, name, icd11Code, diagnosisText,
				attendingDoctor, attendingNurse, phoneNumber, roomNumber);
	}

	private void bindFormFields() {
		binder = new Binder<>(Patient.class);
		binder.forMemberField(medicalRecordNumber)
				.asRequired("Please enter a medical record number.");
		binder.forMemberField(name).asRequired("Please enter a name.");
		binder.bindInstanceFields(this);
	}

	private void addButtonBar() {
		saveBtn = new Button("Save");
		saveBtn.setData("ui:view:patients:savePatientButton");
		saveBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
		saveBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		saveBtn.addClickListener(e -> this.onSave());

		deleteBtn = new Button("Delete");
		deleteBtn.setData("ui:view:patients:deletePatientButton");
		deleteBtn.addClickListener(e -> this.onDelete());

		final HorizontalLayout buttonBar = new HorizontalLayout(saveBtn, deleteBtn);
		addComponent(buttonBar);
	}

	protected abstract void updateFieldVisibility();

	protected abstract void updateFieldEnabling();

	protected abstract void updateButtonVisibility();

	void show(Patient patient) {
		this.patient = patient;
		this.unmodifiedPatient = Patient.clone(patient);
		binder.setBean(patient);

		updateFieldVisibility();
		updateFieldEnabling();
		updateButtonVisibility();

		setVisible(true);
	}

	void hide() {
		setVisible(false);
	}

	private void onSave() {
		try {
			if (patient.getId() == null) {
				if (binder.isValid()) {
					controller.createPatient(patient);
				}
				else {
					Notification.show("Please enter values for all required fields.",
							Notification.Type.WARNING_MESSAGE);
					return;
				}
			}
			else {
				controller.updatePatient(patient, this);
			}
		}
		catch (AccessDeniedException e) {
			SecurityUtils.notifyNotAuthorized();
		}
		refreshCallback.refresh();
		hide();
	}

	private void onDelete() {
		try {
			controller.deletePatient(patient);
			refreshCallback.refresh();
		}
		catch (AccessDeniedException e) {
			SecurityUtils.notifyNotAuthorized();
		}
		hide();
	}

	public boolean hasNameBeenModified() {
		return !Objects.equals(name.getValue(), unmodifiedPatient.getName());
	}

	public boolean hasIcd11CodeBeenModified() {
		return !Objects.equals(icd11Code.getValue(), unmodifiedPatient.getIcd11Code());
	}

	public boolean hasDiagnosisTextBeenModified() {
		return !Objects.equals(diagnosisText.getValue(),
				unmodifiedPatient.getDiagnosisText());
	}

	public boolean hasAttendingDoctorBeenModified() {
		return !Objects.equals(attendingDoctor.getValue(),
				unmodifiedPatient.getAttendingDoctor());
	}

	public boolean hasAttendingNurseBeenModified() {
		return !Objects.equals(attendingNurse.getValue(),
				unmodifiedPatient.getAttendingNurse());
	}

	public boolean hasPhoneNumberBeenModified() {
		return !Objects.equals(phoneNumber.getValue(),
				unmodifiedPatient.getPhoneNumber());
	}

	public boolean hasRoomNumberBeenModified() {
		return !Objects.equals(roomNumber.getValue(), unmodifiedPatient.getRoomNumber());
	}

	@FunctionalInterface
	public interface RefreshCallback {

		void refresh();

	}

	private static class RequiredValidator implements Validator<String> {

		@Override
		public ValidationResult apply(String value, ValueContext context) {
			if (value == null || value.trim().length() == 0) {
				return ValidationResult.error("Please enter a value.");
			}
			else {
				return ValidationResult.ok();
			}
		}

	}

}
