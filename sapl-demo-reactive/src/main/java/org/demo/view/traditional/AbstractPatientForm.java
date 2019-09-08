package org.demo.view.traditional;

import java.util.Objects;

import org.demo.domain.Patient;
import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.demo.view.traditional.singlerequest.SingleRequestStreamManager;
import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public abstract class AbstractPatientForm extends FormLayout {

	@FunctionalInterface
	public interface RefreshCallback {
		void refresh();
	}

	protected TextField medicalRecordNumber;

	protected TextField name;

	protected TextField icd11Code;

	protected TextField diagnosisText;

	protected TextField attendingDoctor;

	protected TextField attendingNurse;

	protected TextField phoneNumber;

	protected TextField roomNumber;

	private Binder<Patient> binder;

	private Button saveBtn;

	private Button deleteBtn;

	private UIController controller;

	private RefreshCallback refreshCallback;

	private ObjectMapper objectMapper;

	protected Patient patient;

	private Patient unmodifiedPatient;

	protected AbstractPatientForm(UIController controller, RefreshCallback refreshCallback) {
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
		saveBtn.addClickListener(e -> onSave());

		deleteBtn = new Button("Delete");
		deleteBtn.setData("ui:view:patients:deletePatientButton");
		deleteBtn.addClickListener(e -> onDelete());

		final HorizontalLayout buttonBar = new HorizontalLayout(saveBtn, deleteBtn);
		addComponent(buttonBar);
	}

	private void onSave() {
		try {
			if (isNewPatient()) {
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

	protected abstract void updateFieldVisibility();

	protected abstract void updateFieldEnabling();

	private void updateButtonVisibility() {
		saveBtn.setVisible(isPermitted(isNewPatient() ? "useForCreate" : "useForUpdate", saveBtn));
		deleteBtn.setVisible(!isNewPatient() && isPermitted("use", deleteBtn, patient.getId()));
	}

	protected boolean isPermitted(String action, AbstractComponent resource) {
		return getSession().getAttribute(SingleRequestStreamManager.class)
				.isAccessPermitted(action, resource.getData());
	}

	protected boolean isPermitted(String action, AbstractComponent resource, Object environment) {
		return getSession().getAttribute(SingleRequestStreamManager.class)
				.isAccessPermitted(action, resource.getData(), environment);
	}

	protected boolean isNewPatient() {
		return patient.getId() == null;
	}

	public boolean hasNameBeenModified() {
		return !Objects.equals(name.getValue(), unmodifiedPatient.getName());
	}

	public boolean hasIcd11CodeBeenModified() {
		return !Objects.equals(icd11Code.getValue(), unmodifiedPatient.getIcd11Code());
	}

	public boolean hasDiagnosisTextBeenModified() {
		return !Objects.equals(diagnosisText.getValue(), unmodifiedPatient.getDiagnosisText());
	}

	public boolean hasAttendingDoctorBeenModified() {
		return !Objects.equals(attendingDoctor.getValue(), unmodifiedPatient.getAttendingDoctor());
	}

	public boolean hasAttendingNurseBeenModified() {
		return !Objects.equals(attendingNurse.getValue(), unmodifiedPatient.getAttendingNurse());
	}

	public boolean hasPhoneNumberBeenModified() {
		return !Objects.equals(phoneNumber.getValue(), unmodifiedPatient.getPhoneNumber());
	}

	public boolean hasRoomNumberBeenModified() {
		return !Objects.equals(roomNumber.getValue(), unmodifiedPatient.getRoomNumber());
	}

}
