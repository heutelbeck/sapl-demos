package io.sapl.demo.spring.domain;

import java.util.List;

public final class Documents {

    private Documents() {}

    public record Document(String id, String title, String classification) {}

    public static final List<Document> ALL = List.of(
            new Document("DOC-1", "Company Newsletter", "PUBLIC"),
            new Document("DOC-2", "Team Standup Notes", "INTERNAL"),
            new Document("DOC-3", "Patient Records", "CONFIDENTIAL"),
            new Document("DOC-4", "Encryption Keys", "SECRET")
    );

}
