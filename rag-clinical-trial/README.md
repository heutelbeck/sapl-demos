# Demo: SAPL-Enforced Access Control in a RAG Pipeline

This demo showcases document-level access control in a Retrieval-Augmented Generation (RAG) pipeline for a clinical trial AI assistant. SAPL policies dynamically filter which documents the LLM can see based on the user's role, site assignment, and declared purpose.

## What This Demo Demonstrates

- **Document-Level Access Control**: SAPL obligations modify pgvector search filters before retrieval, ensuring the LLM never sees unauthorized documents
- **Role/Site/Purpose-Based Filtering**: Three-dimensional access control matrix (Chief Investigator, Site Investigator, Statistician x site x purpose)
- **Method-Level Security with `@PreEnforce`**: Reactive method interception on the document retrieval service
- **Custom Constraint Handler**: `DocumentFilterConstraintHandlerProvider` translates SAPL obligations into pgvector `FilterExpression` objects
- **GDPR Purpose Limitation**: The participant registry (containing real names and contact details) is only accessible to the Chief Investigator when the purpose is adverse event handling
- **Spring AI RAG Pipeline**: Ollama chat model with pgvector for vector similarity search over markdown clinical trial documents
- **Live Security Override**: A UI toggle to bypass SAPL enforcement for demonstration purposes

## Prerequisites

- JDK 21 or newer
- Maven
- Docker (for PostgreSQL/pgvector and optionally Ollama)

## Running the Demo

The demo supports three model provider profiles. The default is `ollama-docker`.

### Ollama in Docker (default)

Starts an Ollama container automatically (with GPU fallback to CPU) and pulls models on first run. PostgreSQL/pgvector also runs in a testcontainer.

```bash
mvn spring-boot:run -pl rag-clinical-trial
```

### Ollama local

Uses a locally installed Ollama instance at `localhost:11434`. You must have Ollama running with the `qwen3:8b` and `nomic-embed-text` models available.

```bash
mvn spring-boot:run -pl rag-clinical-trial -Dspring-boot.run.profiles=ollama-local
```

### Anthropic (Claude Haiku)

Uses Claude Haiku for chat and a Docker Ollama container for embeddings (pgvector requires a local embedding model). Requires an `ANTHROPIC_API_KEY` environment variable.

```bash
export ANTHROPIC_API_KEY=sk-ant-...
mvn spring-boot:run -pl rag-clinical-trial -Dspring-boot.run.profiles=anthropic
```

Access the application at [http://localhost:8080](http://localhost:8080).

On first startup, the application ingests the clinical trial corpus into pgvector and pulls Ollama models if needed. Subsequent starts skip ingestion (idempotent) and reuse cached models via a named Docker volume.

### Demo Users

Select a user from the dropdown (no login required):

| User | Role | Site | Description |
|------|------|------|-------------|
| Dr. Elena Fischer | Chief Investigator | All Sites | Full access; registry only for AE purpose |
| Dr. Thomas Brandt | Site Investigator | Heidelberg | Own site data only; no registry |
| Dr. Emily Crawford | Site Investigator | Edinburgh | Own site data only; no registry |
| Prof. Klaus Richter | Statistician | All Sites | PHQ-9 and protocol only |

### Access Control Matrix

| Role | Protocol | PHQ-9 Data | Adverse Events | Participant Registry |
|------|----------|------------|----------------|----------------------|
| Chief Investigator | All sites | All sites | All sites | AE purpose only |
| Site Investigator | All sites | Own site | Own site | No access |
| Statistician | All sites | All sites | No access | No access |

## Key Concepts

### SAPL Policy-Driven Document Filtering

The SAPL policy set evaluates the user's role, site, and purpose to decide which document types to exclude from the RAG context. Filtering is expressed as **obligations** attached to permit decisions:

```
policy "site-investigator-statistical-analysis"
permit
  subject.principal.role == "Site Investigator";
  subject.principal.purpose == "STATISTICAL_ANALYSIS";
obligation
  { "type": "filterDocuments", "filterSite": subject.principal.site, "excludeTypes": ["registry"] }
```

The obligation instructs the constraint handler to restrict results to the investigator's own site and exclude registry documents.

### Constraint Handler: Obligation to Filter Expression

`DocumentFilterConstraintHandlerProvider` implements `MethodInvocationConstraintHandlerProvider`. It intercepts the `@PreEnforce`-secured `retrieve()` method and modifies the `SearchRequest` Mono before it reaches the vector store:

```java
@PreEnforce(action = "'retrieve'", environment = "{'securityActive': #securityActive}")
Mono<List<Document>> retrieve(Mono<SearchRequest> searchRequest, boolean securityActive) {
    return searchRequest.flatMap(request ->
            Mono.fromCallable(() -> vectorStore.similaritySearch(request))
                    .subscribeOn(Schedulers.boundedElastic()));
}
```

The handler translates `excludeTypes` into `NE` (not-equals) filter expressions and `filterSite` into `EQ` expressions, combining them with AND/OR logic. A workaround distributes AND over OR to avoid a jsonpath operator precedence issue in PgVectorStore.

### Security Context Propagation

The Vaadin UI creates a `UsernamePasswordAuthenticationToken` with a `DemoPrincipal` record as principal. This is injected into the reactive chain via `contextWrite(ReactiveSecurityContextHolder.withAuthentication(...))`, making it available to `@PreEnforce` for SAPL policy evaluation.

### Corpus Documents

The clinical trial corpus consists of five markdown files ingested into pgvector with metadata tags (`type`, `site`, `sensitivity`):

| File | Type | Site | Sensitivity | Content |
|------|------|------|-------------|---------|
| `study_protocol.md` | protocol | all | low | Study design, endpoints, visit schedule |
| `site_heidelberg_phq9.md` | phq9 | heidelberg | high | PHQ-9 scores for participants P-001 to P-005 |
| `site_edinburgh_phq9.md` | phq9 | edinburgh | high | PHQ-9 scores for participants P-006 to P-010 |
| `adverse_events.md` | adverse_event | all | high | Reported adverse events across both sites |
| `participant_registry.md` | registry | all | critical | Real names, dates of birth, email addresses |


## Dependencies

Key dependencies beyond the standard Spring Boot starter:

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
</dependency>
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-spring-boot-starter</artifactId>
</dependency>
```

## Configuration

Configuration is split across profile-specific YAML files:

| File | Contents |
|------|----------|
| `application.yml` | Shared config: server port, pgvector, SAPL PDP, Vaadin, logging |
| `application-ollama-docker.yml` | Ollama chat (qwen3:8b) + embedding (nomic-embed-text) |
| `application-ollama-local.yml` | Same as ollama-docker (container not started due to profile) |
| `application-anthropic.yml` | Anthropic chat (Claude Haiku) + Ollama embedding (nomic-embed-text) |

Each profile excludes the unused chat auto-configuration to prevent conflicting `ChatModel` beans. The `anthropic` profile still starts an Ollama container for embeddings.
