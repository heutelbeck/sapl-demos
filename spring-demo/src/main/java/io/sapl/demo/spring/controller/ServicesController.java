package io.sapl.demo.spring.controller;

import java.util.List;

import io.sapl.demo.spring.domain.Patients.Patient;
import io.sapl.demo.spring.service.PatientService;
import io.sapl.demo.spring.service.PatientService.PatientSummary;
import io.sapl.demo.spring.service.PatientService.TransferResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
class ServicesController {

    private final PatientService patientService;

    @GetMapping("/patients")
    Mono<List<Patient>> listPatients() {
        return patientService.listPatients();
    }

    @GetMapping("/patients/find")
    Mono<List<Patient>> findPatient(@RequestParam(defaultValue = "") String name) {
        return patientService.findPatient(name);
    }

    @GetMapping("/patients/search")
    Mono<List<Patient>> searchPatients(@RequestParam(name = "q", defaultValue = "") String query) {
        return patientService.searchPatients(query);
    }

    @GetMapping("/patients/{patientId}")
    Mono<Patient> getPatientDetail(@PathVariable String patientId) {
        return patientService.getPatientDetail(patientId)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found")));
    }

    @GetMapping("/patients/{patientId}/summary")
    Mono<PatientSummary> getPatientSummary(@PathVariable String patientId) {
        return patientService.getPatientSummary(patientId)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found")));
    }

    @PostMapping("/transfer")
    Mono<TransferResult> transfer(@RequestParam(defaultValue = "10000.0") Double amount) {
        return patientService.doTransfer(amount, "default-account");
    }

}
