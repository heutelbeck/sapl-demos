package io.sapl.demo.view.traditional;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Response;
import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.security.SecurityUtils;
import io.sapl.spring.SAPLAuthorizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class PatientForm extends FormLayout {

    private final RefreshCallback refreshCallback;
    private final PatientRepo patientRepo;
    private final SAPLAuthorizer authorizer;

    private TextField id;
    private TextField name;
    private TextField diagnosis;
    private TextField healthRecordNumber;
    private TextField blackenedHRN;
    private TextField phoneNumber;
    private TextField attendingDoctor;
    private TextField attendingNurse;
    private TextField roomNumber;

    private Binder<Patient> binder;

    private Button saveBtn;
    private Button deleteBtn;

    private Patient patient;
    private boolean isNewPatient;

    PatientForm(RefreshCallback refreshCallback, PatientRepo patientRepo, SAPLAuthorizer authorizer) {
        this.refreshCallback = refreshCallback;
        this.patientRepo = patientRepo;
        this.authorizer = authorizer;

        setSizeFull();

        addFormFields();
        bindFormFields();
        addButtonBar();
    }

    private void addFormFields() {
        id = new TextField("ID");
        name = new TextField("Name");
        diagnosis = new TextField("Diagnosis");
        healthRecordNumber = new TextField("HRN");
        blackenedHRN = new TextField("HRN");
        phoneNumber = new TextField("Phone");
        attendingDoctor = new TextField("Attending Doctor");
        attendingNurse = new TextField("Attending Nurse");
        roomNumber = new TextField("Room Number");

        id.setSizeFull();
        name.setSizeFull();
        diagnosis.setSizeFull();
        healthRecordNumber.setSizeFull();
        blackenedHRN.setSizeFull();
        phoneNumber.setSizeFull();
        attendingDoctor.setSizeFull();
        attendingNurse.setSizeFull();
        roomNumber.setSizeFull();

        addComponents(id, name, diagnosis, healthRecordNumber, blackenedHRN, phoneNumber, attendingDoctor, attendingNurse, roomNumber);
    }

    private void bindFormFields() {
        binder = new Binder<>(Patient.class);
        binder.forField(id)
                .withConverter(new StringToIntegerConverter("Must enter a number"))
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

    void setPatient(Patient patient, boolean isNewPatient) {
        this.patient = patient;
        this.isNewPatient = isNewPatient;
        binder.setBean(patient);

        updateFieldEnabling(isNewPatient);
        updateFieldVisibility(isNewPatient);
        updateButtonVisibility(isNewPatient);

        setVisible(true);
    }

    private void updateFieldEnabling(boolean isNewPatient) {
        id.setEnabled(false);
        blackenedHRN.setEnabled(false);

        if (isNewPatient) {
            name.setEnabled(true);
            diagnosis.setEnabled(false);
            healthRecordNumber.setEnabled(true);
            phoneNumber.setEnabled(true);
            attendingDoctor.setEnabled(true);
            attendingNurse.setEnabled(true);
            roomNumber.setEnabled(true);
        } else {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            name.setEnabled(authorizer.wouldAuthorize(authentication, "update", "name"));
            diagnosis.setEnabled(authorizer.wouldAuthorize(authentication, "updateDiagnosis", patient));
            healthRecordNumber.setEnabled(authorizer.wouldAuthorize(authentication, "update", "HRN"));
            phoneNumber.setEnabled(authorizer.wouldAuthorize(authentication, "update", "phoneNumber"));
            attendingDoctor.setEnabled(authorizer.wouldAuthorize(authentication, "update", "attendingDoctor"));
            attendingNurse.setEnabled(authorizer.wouldAuthorize(authentication, "update", "attendingNurse"));
            roomNumber.setEnabled(authorizer.wouldAuthorize(authentication, "update", "roomNumber"));
        }
    }

    private void updateFieldVisibility(boolean isNewPatient) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isNewPatient) {
            id.setVisible(false);
            name.setVisible(true);
            diagnosis.setVisible(false);
            healthRecordNumber.setVisible(true);
            blackenedHRN.setVisible(false);
            phoneNumber.setVisible(true);
            attendingDoctor.setVisible(true);
            attendingNurse.setVisible(true);
            roomNumber.setVisible(true);
        } else {
            id.setVisible(true);
            name.setVisible(authorizer.authorize(authentication, "read", "name"));
            diagnosis.setVisible(authorizer.authorize(authentication, "read", "diagnosis"));
            healthRecordNumber.setVisible(authorizer.authorize(authentication, "read", "HRN"));
            final Response readBlackenedHRN = authorizer.getResponse(authentication, "readBlackenedHRN", patient);
            if (readBlackenedHRN.getDecision() == Decision.PERMIT) {
                final JsonNode patientNode = readBlackenedHRN.getResource().get();
                blackenedHRN.setValue(patientNode.get("healthRecordNumber").asText());
                blackenedHRN.setVisible(true);
            } else {
                blackenedHRN.setVisible(false);
            }
            phoneNumber.setVisible(authorizer.authorize(authentication, "read", "phoneNumber"));
            attendingDoctor.setVisible(authorizer.authorize(authentication, "read", "attendingDoctor"));
            attendingNurse.setVisible(authorizer.authorize(authentication, "read", "attendingNurse"));
            roomNumber.setVisible(authorizer.authorize(authentication, "readRoomNumber", patient));
        }
    }

    private void updateButtonVisibility(boolean isNewPatient) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        saveBtn.setVisible(authorizer.wouldAuthorize(authentication, isNewPatient ? "create" : "update", "profile"));
        deleteBtn.setVisible(! isNewPatient && authorizer.wouldAuthorize(authentication, "delete", "profile"));
    }

    private void onSave() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authorizer.authorize(authentication, isNewPatient ? "create" : "update", "profile")) {
            patientRepo.save(patient);
            refreshCallback.refresh();
        } else {
            SecurityUtils.notifyNotAuthorized();
        }
        setVisible(false);
    }

    private void onDelete() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authorizer.authorize(authentication, "delete", "profile")) {
            patientRepo.delete(patient);
            refreshCallback.refresh();
        } else {
            SecurityUtils.notifyNotAuthorized();
        }
        setVisible(false);
    }


    @FunctionalInterface
    public interface RefreshCallback {
        void refresh();
    }
}
