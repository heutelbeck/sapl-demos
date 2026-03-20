package io.sapl.demo.spring.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public final class Patients {

    private Patients() {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Patient(String id, String name, String ssn, String diagnosis,
                          String classification, String email,
                          @JsonProperty("internal_notes") String internalNotes) {}

    public static final List<Patient> ALL = List.of(
            new Patient("P-001", "Jane Doe", "123-45-6789", "healthy", "INTERNAL",
                    "jane.doe@example.com", "Follow-up scheduled for next week"),
            new Patient("P-002", "John Smith", "987-65-4321", "checkup", "CONFIDENTIAL",
                    "john.smith@example.com", null),
            new Patient("P-003", "Alice Johnson", "555-12-3456", "healthy", "PUBLIC",
                    "alice.j@example.com", null)
    );

    public static Optional<Patient> findById(String id) {
        return ALL.stream().filter(p -> p.id().equals(id)).findFirst();
    }

}
