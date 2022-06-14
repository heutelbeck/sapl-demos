package io.sapl.demo.axon.intrface.rest;

import java.util.List;
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

import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.MedicalRecordSummary;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.PulseRecord;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Profile("client")
@RequiredArgsConstructor
public class AxonRestController {

	private final DemoService demoService;

	@PostMapping("medicalrecords")
	@Operation(summary = "Create a medical record", description = "some test description")
	void createMedicalRecords(@RequestParam String id, @RequestParam String name) {
		demoService.createMedicalRecord(id, name);
	}

	@GetMapping("medicalrecords")
	@Operation(summary = "Create a medical record", description = "some test description")
	public Mono<List<MedicalRecordSummary>> fetchMedicalRecords() {
		return demoService.getMedicalRecords();
	}

	@PostMapping("medicalrecords/clinical")
	@Operation(summary = "Create a medical record with a clinical record")
	void createMedicalRecordsWithClinical(@RequestParam String id, @RequestParam String name,
			@RequestParam boolean hasClinicalRecord) {
		demoService.createMedicalRecordWithClinical(id, name, hasClinicalRecord);
	}

	@PutMapping("medicalrecords/{id}")
	@Operation(summary = "Update a medical record by its id", description = "Contains ConstraintHandler scenario 1:"
			+ " Patient with id=42 participates in a  pharmaceutical trial. Her medical data must be logged for later auditing."
			+ "You will a see a message being logged. <br>"
			+ "Contains ConstraintHandler scenario 2: If you authenticate as 'doctorOnProbation'."
			+ "Doctors on probation should only apply medical treatment to patients with a clinical record appended. If it is not available the record will be created.")
	void updateMedicalRecords(@PathVariable String id, @RequestParam double pulse,
			@RequestParam double oxygenSaturation) {
		demoService.updateMedicalRecord(id, pulse, oxygenSaturation);
	}

	@PutMapping("medicalrecords-constraint/{id}")
	void updateMedicalRecordsConstraintAnnotation(@PathVariable String id, @RequestParam double pulse,
			@RequestParam double oxygenSaturation) {
		demoService.updateMedicalRecordConstraintAnnotation(id, pulse, oxygenSaturation);
	}

	@PostMapping("medicalrecords/{id}/bloodcount")
	@Operation(summary = "Create a blood count report for a medical record with id")
	void createBloodCountExamination(@PathVariable String id, @RequestParam int examinationId) {
		demoService.createBloodCountExamination(id, examinationId);
	}

	@GetMapping(value = "medicalrecords/{id}/pulse")
	@Operation(summary = "Get a patient's pulse data.", description = "Contains ConstraintHandler scenario 3:"
			+ " If you authenticate as 'externalService'. This serve must only see mapped pulse data (1, 2 or 3).")
	public PulseRecord getBloodCountById(@PathVariable String id) throws ExecutionException, InterruptedException {
		return demoService.getPulseById(id).get();
	}

	@PutMapping("medicalrecords/{id}/bloodcount/{examinationId}")
	@Operation(summary = "Update a blood count by id for medical record with id")
	void updateBloodCountExamination(@PathVariable String id, @PathVariable int examinationId,
			@RequestParam double hematocrit) {
		demoService.updateBloodCount(id, examinationId, hematocrit);
	}

	@GetMapping(value = "medicalrecords/{id}")
	@Operation(summary = "Get a medical record by its id")
	public MedicalRecordSummary getCurrentMedicalRecordByID(@PathVariable String id)
			throws ExecutionException, InterruptedException {
		return demoService.getMedicalRecordById(id).get();
	}

	@Operation(summary = "Subscribe to a medical record by id")
	@GetMapping(value = "medicalrecords/{id}/subscribe", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<ServerSentEvent<String>> subscribeToMedicalRecordByID(@PathVariable String id) {
		return demoService.subscribeToMedicalRecord(id)
				.map(value -> ServerSentEvent.<String>builder().data(String.valueOf(value)).build());
	}

	@Operation(summary = "Subscribe to a pulse value of a medical record by id")
	@GetMapping(value = "medicalrecords/{id}/pulse/subscribe", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<ServerSentEvent<String>> subscribeToScenario2(@PathVariable String id) {
		return demoService.subscribeToPulse(id)
				.map(value -> ServerSentEvent.<String>builder().data(String.valueOf(value)).build());
	}

	@Operation(summary = "Subscribe to an oxygen saturation value of a medical record by id")
	@GetMapping(value = "medicalrecords/{id}/oxygensaturation/subscribe", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<ServerSentEvent<String>> subscribeToScenario3(@PathVariable String id) {
		return demoService.subscribeToOxygenSaturation(id)
				.map(value -> ServerSentEvent.<String>builder().data(String.valueOf(value)).build());
	}

	@GetMapping(value = "stopUpdates")
	@Operation(summary = "Stop sending update commands")
	public void stopUpdates() {
		demoService.stopUpdates();
	}

}
