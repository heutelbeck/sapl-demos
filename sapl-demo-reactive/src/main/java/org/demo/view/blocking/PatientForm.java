package org.demo.view.blocking;

import java.util.Objects;
import java.util.Optional;

import org.demo.domain.PatientRepo;
import org.demo.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Response;
import io.sapl.pep.BlockingSAPLAuthorizer;

class PatientForm extends AbstractPatientForm {

    private RefreshCallback refreshCallback;
    private PatientRepo patientRepo;
    private BlockingSAPLAuthorizer authorizer;

    PatientForm(RefreshCallback refreshCallback, PatientRepo patientRepo, BlockingSAPLAuthorizer authorizer) {
        this.refreshCallback = refreshCallback;
        this.patientRepo = patientRepo;
        this.authorizer = authorizer;
    }

    protected void updateFieldEnabling(boolean isNewPatient) {
        id.setEnabled(false);
        blackenedHRN.setEnabled(false);

        if (isNewPatient) {
            name.setEnabled(true);
            diagnosis.setEnabled(false); // only the admin can create a new patient, but doctors add a diagnosis
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

    protected void updateFieldVisibility(boolean isNewPatient) {
        if (isNewPatient) {
            id.setVisible(false);
            name.setVisible(true);
            diagnosis.setVisible(false); // only the admin can create a new patient, but doctors add a diagnosis
            healthRecordNumber.setVisible(true);
            blackenedHRN.setVisible(false);
            phoneNumber.setVisible(true);
            attendingDoctor.setVisible(true);
            attendingNurse.setVisible(true);
            roomNumber.setVisible(true);
        } else {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            id.setVisible(true);
            name.setVisible(authorizer.authorize(authentication, "read", "name"));
            diagnosis.setVisible(authorizer.authorize(authentication, "read", "diagnosis"));
            healthRecordNumber.setVisible(authorizer.authorize(authentication, "read", "HRN"));
            final Response readBlackenedHRN = authorizer.getResponse(authentication, "readBlackenedHRN", patient);
            if (readBlackenedHRN.getDecision() == Decision.PERMIT) {
                final Optional<JsonNode> patientNode = readBlackenedHRN.getResource();
                blackenedHRN.setValue(patientNode.map(node -> node.get("healthRecordNumber").asText()).orElse(""));
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

    protected void updateButtonVisibility(boolean isNewPatient) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        saveBtn.setVisible(authorizer.wouldAuthorize(authentication, isNewPatient ? "create" : "update", "profile"));
        deleteBtn.setVisible(! isNewPatient && authorizer.wouldAuthorize(authentication, "delete", "profile"));
    }

    protected void onSave() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authorizer.authorize(authentication, isNewPatient ? "create" : "update", "profile")) {

            // get the update authorization for each modified field to enforce obligation handling
            // and reset the field if necessary
            if (! Objects.equals(name.getValue(), unmodifiedPatient.getName())) {
                if (! authorizer.authorize(authentication, "update", "name")) {
                    patient.setName(unmodifiedPatient.getName());
                }
            }
            if (! Objects.equals(diagnosis.getValue(), unmodifiedPatient.getDiagnosis())) {
                if (! authorizer.authorize(authentication, "updateDiagnosis", patient)) {
                    patient.setDiagnosis(unmodifiedPatient.getDiagnosis());
                }
            }
            if (! Objects.equals(healthRecordNumber.getValue(), unmodifiedPatient.getHealthRecordNumber())) {
                if (! authorizer.authorize(authentication, "update", "HRN")) {
                    patient.setHealthRecordNumber(unmodifiedPatient.getHealthRecordNumber());
                }
            }
            if (! Objects.equals(phoneNumber.getValue(), unmodifiedPatient.getPhoneNumber())) {
                if (! authorizer.authorize(authentication, "update", "phoneNumber")) {
                    patient.setPhoneNumber(unmodifiedPatient.getPhoneNumber());
                }
            }
            if (! Objects.equals(attendingDoctor.getValue(), unmodifiedPatient.getAttendingDoctor())) {
                if (! authorizer.authorize(authentication, "update", "attendingDoctor")) {
                    patient.setAttendingDoctor(unmodifiedPatient.getAttendingDoctor());
                }
            }
            if (! Objects.equals(attendingNurse.getValue(), unmodifiedPatient.getAttendingNurse())) {
                if (! authorizer.authorize(authentication, "update", "attendingNurse")) {
                    patient.setAttendingNurse(unmodifiedPatient.getAttendingNurse());
                }
            }
            if (! Objects.equals(roomNumber.getValue(), unmodifiedPatient.getRoomNumber())) {
                if (! authorizer.authorize(authentication, "update", "roomNumber")) {
                    patient.setRoomNumber(unmodifiedPatient.getRoomNumber());
                }
            }

            patientRepo.save(patient);
            refreshCallback.refresh();
        } else {
            SecurityUtils.notifyNotAuthorized();
        }
        setVisible(false);
    }

    protected void onDelete() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authorizer.authorize(authentication, "delete", "profile")) {
            patientRepo.delete(patient);
            refreshCallback.refresh();
        } else {
            SecurityUtils.notifyNotAuthorized();
        }
        setVisible(false);
    }
}
