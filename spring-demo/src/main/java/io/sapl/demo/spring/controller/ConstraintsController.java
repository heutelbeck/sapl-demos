package io.sapl.demo.spring.controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sapl.demo.spring.domain.Documents;
import io.sapl.demo.spring.domain.Documents.Document;
import io.sapl.demo.spring.handler.AuditTrailHandler;
import io.sapl.spring.method.metadata.PostEnforce;
import io.sapl.spring.method.metadata.PreEnforce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/constraints")
class ConstraintsController {

    record PatientInfo(String name, String ssn, String email, String diagnosis) {}

    record PatientFull(String name, String ssn, String email, String diagnosis,
                       @JsonProperty("internal_notes") String internalNotes) {}

    record LoggedResponse(String message, Map<String, Object> data) {}

    record AuditedResponse(String message, Map<String, Object> record) {}

    record FinancialRecord(String name, String ssn, String creditCard, String email, double balance) {}

    record TimestampedResponse(String message,
                               @JsonProperty("policy_timestamp") String policyTimestamp,
                               Map<String, Object> data) {}

    record AdvisedResponse(String message, Map<String, Object> data) {}

    record RecordEntry(String id, String value, String classification) {}

    private final AuditTrailHandler auditTrailHandler;

    @GetMapping("/patient")
    @PreEnforce(action = "'readPatient'", resource = "'patient'")
    Mono<PatientInfo> getConstraintPatient() {
        return Mono.just(new PatientInfo("Jane Doe", "123-45-6789", "jane.doe@example.com", "healthy"));
    }

    @GetMapping("/patient-full")
    @PreEnforce(action = "'readPatientFull'", resource = "'patientFull'")
    Mono<PatientFull> getPatientFull() {
        return Mono.just(new PatientFull("Jane Doe", "123-45-6789", "jane.doe@example.com",
                "healthy", "Follow-up scheduled for next week"));
    }

    @GetMapping("/logged")
    @PreEnforce(action = "'readLogged'", resource = "'logged'")
    Mono<LoggedResponse> getLogged() {
        return Mono.just(new LoggedResponse(
                "This response was logged by a policy obligation",
                Map.of("patientId", "P-001", "status", "active")));
    }

    @GetMapping("/audited")
    @PreEnforce(action = "'readAudited'", resource = "'audited'")
    Mono<AuditedResponse> getAudited() {
        return Mono.just(new AuditedResponse(
                "This response was recorded in the audit trail",
                Map.of("id", "MR-42", "type", "blood-work", "result", "normal")));
    }

    @GetMapping("/audit-log")
    Mono<List<?>> getAuditLog() {
        return Mono.just(auditTrailHandler.getAuditLog());
    }

    @GetMapping("/redacted")
    @PreEnforce(action = "'readRedacted'", resource = "'redacted'")
    Mono<FinancialRecord> getRedacted() {
        return Mono.just(new FinancialRecord(
                "John Smith", "987-65-4321", "4111-1111-1111-1111", "john@example.com", 1500.0));
    }

    @GetMapping("/documents")
    @PreEnforce(action = "'readDocuments'", resource = "'documents'")
    Mono<List<Document>> getDocuments() {
        return Mono.just(Documents.ALL);
    }

    @GetMapping("/timestamped")
    @PreEnforce(action = "'readTimestamped'", resource = "'timestamped'")
    Mono<TimestampedResponse> getTimestamped(String policyTimestamp) {
        return Mono.just(new TimestampedResponse(
                "This response includes a policy-injected timestamp",
                policyTimestamp,
                Map.of("sensor", "temp-01", "value", 22.5)));
    }

    @GetMapping("/error-demo")
    @PreEnforce(action = "'readErrorDemo'", resource = "'errorDemo'")
    Mono<Object> getErrorDemo() {
        return Mono.error(new RuntimeException("Simulated backend failure"));
    }

    @GetMapping("/resource-replaced")
    @PreEnforce(action = "'readReplaced'", resource = "'replaced'")
    Mono<Map<String, Object>> getResourceReplaced() {
        return Mono.just(Map.of(
                "message", "You should NOT see this -- the PDP replaces this resource",
                "originalData", true));
    }

    @GetMapping("/advised")
    @PreEnforce(action = "'readAdvised'", resource = "'advised'")
    Mono<AdvisedResponse> getAdvised() {
        return Mono.just(new AdvisedResponse(
                "Access granted despite unhandled advice",
                Map.of("category", "medical", "status", "reviewed")));
    }

    @GetMapping("/record/{recordId}")
    @PostEnforce(action = "'readRecord'",
            resource = "{'type': 'record', 'data': returnObject}")
    Mono<RecordEntry> getRecord(@PathVariable String recordId) {
        log.info("Fetching record {}", recordId);
        return Mono.just(new RecordEntry(recordId, "sensitive-data", "confidential"));
    }

    @GetMapping("/unhandled")
    @PreEnforce(action = "'readSecret'", resource = "'secret'")
    Mono<Object> getUnhandled() {
        return Mono.just("you should not see this");
    }

}
