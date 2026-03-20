package io.sapl.demo.spring.controller;

import java.util.List;

import io.sapl.demo.spring.domain.Patients;
import io.sapl.demo.spring.domain.Patients.Patient;
import io.sapl.demo.spring.service.PatientService;
import io.sapl.demo.spring.service.PatientService.TransferResult;
import io.sapl.spring.method.metadata.PostEnforce;
import io.sapl.spring.method.metadata.PreEnforce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
class BasicController {

    private final PatientService patientService;

    @GetMapping("/api/patient/{patientId}")
    @PreEnforce(action = "'readPatient'", resource = "'patient'")
    Mono<Patient> getPatient(@PathVariable String patientId) {
        return Mono.justOrEmpty(Patients.findById(patientId))
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found")));
    }

    @GetMapping("/api/patients")
    @PostEnforce(action = "'readPatients'", resource = "'patients'")
    Mono<List<Patient>> getPatients() {
        return Mono.just(Patients.ALL);
    }

    @PostMapping("/api/transfer")
    Mono<TransferResult> transfer(
            @RequestParam(defaultValue = "10000.0") Double amount,
            @RequestParam(defaultValue = "default-account") String recipient) {
        return patientService.doTransfer(amount, recipient);
    }

}
