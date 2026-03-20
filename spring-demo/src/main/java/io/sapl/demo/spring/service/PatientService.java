package io.sapl.demo.spring.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sapl.demo.spring.domain.Patients;
import io.sapl.demo.spring.domain.Patients.Patient;
import io.sapl.spring.method.metadata.PostEnforce;
import io.sapl.spring.method.metadata.PreEnforce;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PatientService {

    public record PatientSummary(String id, String name, String ssn, String diagnosis,
                                 String classification, String email,
                                 @JsonProperty("internal_notes") String internalNotes,
                                 String insurance) {

        static PatientSummary from(Patient p, String insurance) {
            return new PatientSummary(p.id(), p.name(), p.ssn(), p.diagnosis(),
                    p.classification(), p.email(), p.internalNotes(), insurance);
        }
    }

    public record TransferResult(double transferred, String recipient, String status) {}

    @PreEnforce(action = "'listPatients'", resource = "'patients'")
    public Mono<List<Patient>> listPatients() {
        return Mono.just(Patients.ALL);
    }

    @PreEnforce(action = "'findPatient'", resource = "'patient'")
    public Mono<List<Patient>> findPatient(String name) {
        var results = Patients.ALL.stream()
                .filter(p -> p.name().toLowerCase().contains(name.toLowerCase()))
                .toList();
        return Mono.just(results);
    }

    @PreEnforce(action = "'searchPatients'", resource = "'patientSearch'")
    public Mono<List<Patient>> searchPatients(String query) {
        var q = query.toLowerCase();
        var results = Patients.ALL.stream()
                .filter(p -> p.name().toLowerCase().contains(q)
                        || p.diagnosis().toLowerCase().contains(q))
                .toList();
        return Mono.just(results);
    }

    @PostEnforce(action = "'getPatientDetail'",
            resource = "{'type': 'patientDetail', 'data': returnObject}")
    public Mono<Patient> getPatientDetail(String patientId) {
        return Mono.justOrEmpty(Patients.findById(patientId));
    }

    @PreEnforce(action = "'getPatientSummary'", resource = "'patientSummary'")
    public Mono<PatientSummary> getPatientSummary(String patientId) {
        return Mono.justOrEmpty(Patients.findById(patientId)
                .map(p -> PatientSummary.from(p, "INS-9876-XYZ")));
    }

    @PreEnforce(action = "'transfer'", resource = "'account'")
    public Mono<TransferResult> doTransfer(Double amount, String recipient) {
        return Mono.just(new TransferResult(amount, recipient, "completed"));
    }

}
