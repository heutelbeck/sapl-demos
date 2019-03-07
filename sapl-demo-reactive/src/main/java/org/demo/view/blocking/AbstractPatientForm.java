package org.demo.view.blocking;

import org.demo.domain.Patient;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public abstract class AbstractPatientForm extends FormLayout {

    protected TextField id;
    protected TextField mrn;
    protected TextField name;
    protected TextField icd11Code;
    protected TextField blackenedIcd11Code;
    protected TextField diagnosisText;
    protected TextField attendingDoctor;
    protected TextField attendingNurse;
    protected TextField phoneNumber;
    protected TextField roomNumber;

    private Binder<Patient> binder;

    protected Button saveBtn;
    protected Button deleteBtn;

    protected Patient patient;
    protected Patient unmodifiedPatient;
    protected boolean isNewPatient;

    protected AbstractPatientForm() {
        setSizeFull();
        setVisible(false);

        addFormFields();
        bindFormFields();
        addButtonBar();
    }

    private void addFormFields() {
        id = new TextField("ID");
        mrn = new TextField("Medical Record Number");
        name = new TextField("Name");
        icd11Code = new TextField("ICD-11");
        blackenedIcd11Code = new TextField("ICD-11");
        diagnosisText = new TextField("Diagnosis");
        attendingDoctor = new TextField("Attending Doctor");
        attendingNurse = new TextField("Attending Nurse");
        phoneNumber = new TextField("Phone");
        roomNumber = new TextField("Room Number");

        id.setSizeFull();
        mrn.setSizeFull();
        name.setSizeFull();
        icd11Code.setSizeFull();
        blackenedIcd11Code.setSizeFull();
        diagnosisText.setSizeFull();
        attendingDoctor.setSizeFull();
        attendingNurse.setSizeFull();
        phoneNumber.setSizeFull();
        roomNumber.setSizeFull();

        addComponents(id, mrn, name, icd11Code, blackenedIcd11Code, diagnosisText, attendingDoctor, attendingNurse, phoneNumber, roomNumber);
    }

    private void bindFormFields() {
        binder = new Binder<>(Patient.class);
        binder.forField(id)
                .withConverter(new StringToLongConverter("Must enter a number"))
                .bind(Patient::getId, Patient::setId);
        binder.bindInstanceFields(this);
    }

    private void addButtonBar() {
        saveBtn = new Button("Save");
        saveBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        saveBtn.addClickListener(e -> this.onSave());

        deleteBtn = new Button("Delete");
        deleteBtn.addClickListener(e -> this.onDelete());

        final HorizontalLayout buttonBar = new HorizontalLayout(saveBtn, deleteBtn);
        addComponent(buttonBar);
    }

    void hide() {
        setVisible(false);
    }

    void show(Patient patient, boolean isNewPatient) {
        this.patient = patient;
        this.unmodifiedPatient = Patient.clone(patient);
        this.isNewPatient = isNewPatient;
        binder.setBean(patient);

        updateFieldEnabling(isNewPatient);
        updateFieldVisibility(isNewPatient);
        updateButtonVisibility(isNewPatient);

        setVisible(true);
    }

    protected abstract void updateFieldEnabling(boolean isNewPatient);

    protected abstract void updateFieldVisibility(boolean isNewPatient);

    protected abstract void updateButtonVisibility(boolean isNewPatient);

    protected abstract void onSave();

    protected abstract void onDelete();


    @FunctionalInterface
    public interface RefreshCallback {
        void refresh();
    }

}
