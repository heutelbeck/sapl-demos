package io.sapl.demo.view.blocking.multirequest;

import static io.sapl.api.pdp.multirequest.IdentifiableAction.CREATE_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableAction.DELETE_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableAction.READ_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableAction.UPDATE_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableSubject.AUTHENTICATION_ID;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Response;
import io.sapl.api.pdp.multirequest.IdentifiableAction;
import io.sapl.api.pdp.multirequest.IdentifiableResource;
import io.sapl.api.pdp.multirequest.IdentifiableSubject;
import io.sapl.api.pdp.multirequest.MultiDecision;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.api.pdp.multirequest.RequestElements;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.security.SecurityUtils;
import io.sapl.demo.view.blocking.AbstractPatientForm;
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

            final MultiRequest multiRequest = new MultiRequest();
            multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
            multiRequest.addAction(new IdentifiableAction(UPDATE_ID, "update"));
            multiRequest.addAction("updateDiagnosis");
            multiRequest.addResource("name");
            multiRequest.addResource(new IdentifiableResource("patient", patient));
            multiRequest.addResource("HRN");
            multiRequest.addResource("phoneNumber");
            multiRequest.addResource("attendingDoctor");
            multiRequest.addResource("attendingNurse");
            multiRequest.addResource("roomNumber");
            multiRequest.addRequest("updateName", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "name"));
            multiRequest.addRequest("updateDiagnosis", new RequestElements(AUTHENTICATION_ID, "updateDiagnosis", "patient"));
            multiRequest.addRequest("updateHRN", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "HRN"));
            multiRequest.addRequest("updatePhoneNumber", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "phoneNumber"));
            multiRequest.addRequest("updateAttendingDoctor", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "attendingDoctor"));
            multiRequest.addRequest("updateAttendingNurse", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "attendingNurse"));
            multiRequest.addRequest("updateRoomNumber", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "roomNumber"));

            final MultiDecision multiDecision = authorizer.wouldAuthorize(multiRequest);

            name.setEnabled(multiDecision.isAccessPermittedForRequestWithId("updateName"));
            diagnosis.setEnabled(multiDecision.isAccessPermittedForRequestWithId("updateDiagnosis"));
            healthRecordNumber.setEnabled(multiDecision.isAccessPermittedForRequestWithId("updateHRN"));
            phoneNumber.setEnabled(multiDecision.isAccessPermittedForRequestWithId("updatePhoneNumber"));
            attendingDoctor.setEnabled(multiDecision.isAccessPermittedForRequestWithId("updateAttendingDoctor"));
            attendingNurse.setEnabled(multiDecision.isAccessPermittedForRequestWithId("updateAttendingNurse"));
            roomNumber.setEnabled(multiDecision.isAccessPermittedForRequestWithId("updateRoomNumber"));
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

            final MultiRequest multiRequest = new MultiRequest();
            multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
            multiRequest.addAction(new IdentifiableAction(READ_ID, "read"));
            multiRequest.addAction("readBlackenedHRN");
            multiRequest.addAction("readRoomNumber");
            multiRequest.addResource("name");
            multiRequest.addResource("diagnosis");
            multiRequest.addResource("HRN");
            multiRequest.addResource(new IdentifiableResource("patient", patient));
            multiRequest.addResource("phoneNumber");
            multiRequest.addResource("attendingDoctor");
            multiRequest.addResource("attendingNurse");
            multiRequest.addResource("roomNumber");
            multiRequest.addRequest("readName", new RequestElements(AUTHENTICATION_ID, READ_ID, "name"));
            multiRequest.addRequest("readDiagnosis", new RequestElements(AUTHENTICATION_ID, READ_ID, "diagnosis"));
            multiRequest.addRequest("readHRN", new RequestElements(AUTHENTICATION_ID, READ_ID, "HRN"));
            multiRequest.addRequest("readBlackenedHRN", new RequestElements(AUTHENTICATION_ID, "readBlackenedHRN", "patient"));
            multiRequest.addRequest("readPhoneNumber", new RequestElements(AUTHENTICATION_ID, READ_ID, "phoneNumber"));
            multiRequest.addRequest("readAttendingDoctor", new RequestElements(AUTHENTICATION_ID, READ_ID, "attendingDoctor"));
            multiRequest.addRequest("readAttendingNurse", new RequestElements(AUTHENTICATION_ID, READ_ID, "attendingNurse"));
            multiRequest.addRequest("readRoomNumber", new RequestElements(AUTHENTICATION_ID, "readRoomNumber", "patient"));

            final MultiResponse responses = authorizer.getResponses(multiRequest);

            id.setVisible(true);
            name.setVisible(responses.isAccessPermittedForRequestWithId("readName"));
            diagnosis.setVisible(responses.isAccessPermittedForRequestWithId("readDiagnosis"));
            healthRecordNumber.setVisible(responses.isAccessPermittedForRequestWithId("readHRN"));
            final Response readBlackenedHRN = responses.getResponseForRequestWithId("readBlackenedHRN");
            if (readBlackenedHRN.getDecision() == Decision.PERMIT) {
                final Optional<JsonNode> patientNode = readBlackenedHRN.getResource();
                blackenedHRN.setValue(patientNode.map(node -> node.get("healthRecordNumber").asText()).orElse(""));
                blackenedHRN.setVisible(true);
            } else {
                blackenedHRN.setVisible(false);
            }
            phoneNumber.setVisible(responses.isAccessPermittedForRequestWithId("readPhoneNumber"));
            attendingDoctor.setVisible(responses.isAccessPermittedForRequestWithId("readAttendingDoctor"));
            attendingNurse.setVisible(responses.isAccessPermittedForRequestWithId("readAttendingNurse"));
            roomNumber.setVisible(responses.isAccessPermittedForRequestWithId("readRoomNumber"));
        }
    }

    protected void updateButtonVisibility(boolean isNewPatient) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final MultiRequest multiRequest = new MultiRequest();
        multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
        multiRequest.addAction(new IdentifiableAction(UPDATE_ID, "update"));
        multiRequest.addAction(new IdentifiableAction(DELETE_ID, "delete"));
        multiRequest.addResource("profile");
        if (isNewPatient) {
            multiRequest.addAction(new IdentifiableAction(CREATE_ID, "create"));
            multiRequest.addRequest("saveProfile", new RequestElements(AUTHENTICATION_ID, CREATE_ID, "profile"));
        } else {
            multiRequest.addAction(new IdentifiableAction(UPDATE_ID, "update"));
            multiRequest.addRequest("saveProfile", new RequestElements(AUTHENTICATION_ID, UPDATE_ID, "profile"));
        }
        multiRequest.addRequest("deleteProfile", new RequestElements(AUTHENTICATION_ID, DELETE_ID, "profile"));

        final MultiDecision multiDecision = authorizer.wouldAuthorize(multiRequest);

        saveBtn.setVisible(multiDecision.isAccessPermittedForRequestWithId("saveProfile"));
        deleteBtn.setVisible(! isNewPatient && multiDecision.isAccessPermittedForRequestWithId("deleteProfile"));
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
