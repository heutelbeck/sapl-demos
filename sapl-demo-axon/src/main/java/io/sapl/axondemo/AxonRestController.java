package io.sapl.axondemo;


import java.util.concurrent.ExecutionException;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.axondemo.domain.MedicalRecordAPI;
import io.sapl.axondemo.domain.MedicalRecordAPI.MedicalRecordSummary;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Profile("client")
@RestController
@RequiredArgsConstructor
public class AxonRestController {

    private final DemoService demoService;

    @Operation(summary = "Create a medical record", description = "some test description")
    @PostMapping("medicalrecords")
    void createMedicalRecords(@RequestParam String id, @RequestParam String name) {
        demoService.createMedicalRecord(id, name);
    }

    @Operation(summary = "Create a medical record with a clinical record")
    @PostMapping("medicalrecords/clinical")
    void createMedicalRecordsWithClinical(@RequestParam String id, @RequestParam String name, @RequestParam boolean hasClinicalRecord) {
        demoService.createMedicalRecordWithClinical(id, name, hasClinicalRecord);
    }

    @Operation(summary = "Update a medical record by its id", description = "Contains ConstraintHandler scenario 1:" +
            " Patient with id=42 participates in a  pharmaceutical trial. Her medical data must be logged for later auditing." +
            "You will a see a message being logged. <br>" +
            "Contains ConstraintHandler scenario 2: If you authenticate as 'doctorOnProbation'." +
            "Doctors on probation should only apply medical treatment to patients with a clinical record appended. If it is not available the record will be created.")
    @PutMapping("medicalrecords/{id}")
    void updateMedicalRecords(@PathVariable String id, @RequestParam double pulse, @RequestParam double oxygenSaturation) {
        demoService.updateMedicalRecord(id, pulse, oxygenSaturation);
    }

    @PutMapping("medicalrecords-constraint/{id}")
    void updateMedicalRecordsConstraintAnnotation(@PathVariable String id, @RequestParam double pulse, @RequestParam double oxygenSaturation) {
        demoService.updateMedicalRecordConstraintAnnotation(id, pulse, oxygenSaturation);
    }

    @Operation(summary = "Create a blood count report for a medical record with id")
    @PostMapping("medicalrecords/{id}/bloodcount")
    void createBloodCountExamination(@PathVariable String id, @RequestParam int examinationId) {
        demoService.createBloodCountExamination(id, examinationId);
    }

    @Operation(summary="Get a patient's pulse data.", description = "Contains ConstraintHandler scenario 3:" +
            " If you authenticate as 'externalService'. This serve must only see mapped pulse data (1, 2 or 3).")
    @GetMapping(value = "medicalrecords/{id}/pulse")
    MedicalRecordAPI.PulseRecord getBloodCountById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return demoService.getPulseById(id).get();
    }

    @Operation(summary = "Update a blood count by id for medical record with id")
    @PutMapping("medicalrecords/{id}/bloodcount/{examinationId}")
    void updateBloodCountExamination(@PathVariable String id, @PathVariable int examinationId, @RequestParam double hematocrit) {
        demoService.updateBloodCount(id, examinationId, hematocrit);
    }

    @Operation(summary = "Get a medical record by its id")
    @GetMapping(value = "medicalrecords/{id}")
    MedicalRecordSummary getCurrentMedicalRecordByID(@PathVariable String id) throws ExecutionException, InterruptedException {
        return demoService.getMedicalRecordById(id).get();
    }

    @Operation(summary = "Subscribe to a medical record by id")
    @GetMapping(value = "medicalrecords/{id}/subscribe", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<ServerSentEvent<String>> subscribeToMedicalRecordByID(@PathVariable String id) {
        return demoService.subscribeToMedicalRecord(id).map(value -> ServerSentEvent.<String>builder().data(String.valueOf(value)).build());
    }

    @Operation(summary = "Subscribe to a pulse value of a medical record by id")
    @GetMapping(value = "medicalrecords/{id}/pulse/subscribe", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<ServerSentEvent<String>> subscribeToScenario2(@PathVariable String id) {
        return demoService.subscribeToPulse(id).map(value -> ServerSentEvent.<String>builder().data(String.valueOf(value)).build());
    }

    @Operation(summary = "Subscribe to an oxygen saturation value of a medical record by id")
    @GetMapping(value = "medicalrecords/{id}/oxygensaturation/subscribe", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<ServerSentEvent<String>> subscribeToScenario3(@PathVariable String id) {
        return demoService.subscribeToOxygenSaturation(id).map(value -> ServerSentEvent.<String>builder().data(String.valueOf(value)).build());
    }
    
    @Operation(summary = "Stop sending update commands")
    @GetMapping(value = "stopUpdates")
    void stopUpdates() {
    	demoService.stopUpdates();
    }

}

