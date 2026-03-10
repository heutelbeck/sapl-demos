# Demo: Human-in-the-Loop Approval for AI Tool Calls

This demo showcases policy-driven human-in-the-loop (HITL) approval for safety-critical AI tool calls in a clinical trial setting. SAPL policies control which tools require human approval before execution, with configurable timeouts and mandatory review flags expressed as policy obligations.

## What This Demo Demonstrates

- **Policy-Driven Tool Gating**: SAPL policies decide per tool whether to permit, deny, or require human approval before execution
- **Obligation-Based Approval**: The `humanApprovalRequired` obligation triggers a blocking approval dialog routed to the originating browser tab
- **Mandatory Review**: The `noAutoApprove` flag in the obligation prevents auto-approve from bypassing the dialog for high-risk actions
- **Configurable Timeouts**: ISO 8601 duration strings in the obligation control how long the system waits before auto-denying
- **Minimal Viable PEP**: Only PERMIT with no resource replacement and all obligations handled results in tool execution; everything else is denied
- **Session-Scoped Routing**: Approval dialogs route to the specific browser tab that initiated the tool call, supporting multiple tabs per user
- **Spring AI Tool Integration**: `@Tool`-annotated methods with SAPL authorization checks and blocking approval flow

## Prerequisites

- JDK 21 or newer
- Maven
- Docker (for Ollama in Docker mode)

## Running the Demo

The demo supports three model provider profiles. The default is `ollama-docker`.

### Ollama in Docker (default)

Starts an Ollama container automatically and pulls the model on first run.

```bash
mvn spring-boot:run -pl hitl-clinical-trial
```

### Ollama local

Uses a locally installed Ollama instance at `localhost:11434`. You must have Ollama running with the `qwen3:8b` model available.

```bash
mvn spring-boot:run -pl hitl-clinical-trial -Dspring-boot.run.profiles=ollama-local
```

### Anthropic (Claude Haiku)

Uses Claude Haiku for chat. Requires an `ANTHROPIC_API_KEY` environment variable.

```bash
export ANTHROPIC_API_KEY=sk-ant-...
mvn spring-boot:run -pl hitl-clinical-trial -Dspring-boot.run.profiles=anthropic
```

Access the application at [http://localhost:8080](http://localhost:8080).

### Demo Users

Select a user from the dropdown (no login required):

| User | Role |
|------|------|
| Dr. Elena Fischer | Safety Officer |
| Dr. Marcus Brandt | Site Investigator |

### Auto-Approve Toggle

A checkbox in the UI enables auto-approve mode. When enabled, tools with the `humanApprovalRequired` obligation are approved automatically unless the policy sets `noAutoApprove: true`.

## Key Concepts

### SAPL Policy-Driven Tool Authorization

The policy set uses `first or abstain` combining and scopes to the six tool names. Read tools are permitted without obligations. Action tools attach obligations that the PEP must handle:

```
policy "permit-notify-with-approval"
permit
  action == "notifyParticipant";
obligation
  { "type": "humanApprovalRequired" }

policy "permit-suspend-with-mandatory-approval"
permit
  action == "suspendParticipant";
obligation
  { "type": "humanApprovalRequired", "noAutoApprove": true, "timeout": "PT120S" }
```

The `notifyParticipant` tool requires approval but can be auto-approved. The `suspendParticipant` tool forces a human to explicitly approve within 120 seconds.

### Tool Authorization Matrix

| Tool | Decision | Approval Required | Auto-Approvable | Timeout |
|------|----------|-------------------|-----------------|---------|
| `listAdverseEvents` | Permit | No | - | - |
| `getAdverseEvent` | Permit | No | - | - |
| `getSafetyGuidelines` | Permit | No | - | - |
| `exportSafetyReport` | Permit | No | - | - |
| `notifyParticipant` | Permit | Yes | Yes | 60s (default) |
| `suspendParticipant` | Permit | Yes | No | 120s |

### Minimal Viable PEP

Each action tool calls `checkPolicy()` which constructs an `AuthorizationSubscription` from the authenticated principal (subject), tool name (action), and tool parameters (resource), then evaluates it against the PDP:

```java
val subscription = AuthorizationSubscription.of(principal, toolName, resource);
val decision = pdp.decideOnce(subscription).block();
if (decision == null || decision.decision() != Decision.PERMIT
        || !(decision.resource() instanceof UndefinedValue)) {
    return false;
}
for (val obligation : decision.obligations()) {
    if (!handleObligation(obligation, toolName, resource)) {
        return false;
    }
}
```

The PEP is fail-closed: any unhandled obligation, resource replacement, or non-PERMIT decision results in denial.

### Blocking Approval Flow

When `handleObligation()` encounters a `humanApprovalRequired` obligation, it blocks the tool execution thread on a `CompletableFuture` and pushes a modal dialog to the originating browser tab via session-scoped listeners. The dialog shows the tool name, parameters, and a countdown timer. The future completes when the user clicks Approve/Deny or the timeout expires (auto-deny).

### Security Context Propagation

The Vaadin UI writes a `DemoPrincipal` and session ID into the Reactor context via `contextWrite()`. A `SecurityContextRestoringToolCallingManager` bridges these from the Reactor context to `ThreadLocal` storage (`SecurityContextHolder` and `SessionIdHolder`) on the tool execution thread, making them available to the PEP.

## Tools

The AI assistant has access to six tools representing clinical trial adverse event management:

| Tool | Type | Description |
|------|------|-------------|
| `listAdverseEvents` | Read | Lists all active adverse events with severity and status |
| `getAdverseEvent` | Read | Retrieves detailed information for a specific event |
| `getSafetyGuidelines` | Read | Returns the study safety response guidelines |
| `notifyParticipant` | Action | Sends a notification to a participant or emergency contact |
| `suspendParticipant` | Action | Suspends a participant from active treatment |
| `exportSafetyReport` | Action | Exports a safety report to the DSMB |

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
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-spring-boot-starter</artifactId>
</dependency>
```

## Configuration

Configuration is split across profile-specific YAML files:

| File | Contents |
|------|----------|
| `application.yml` | Shared config: server port, SAPL PDP, Vaadin, virtual threads, logging |
| `application-ollama-docker.yml` | Ollama chat (qwen3:8b), auto-pull model |
| `application-ollama-local.yml` | Same model config, expects local Ollama instance |
| `application-anthropic.yml` | Anthropic chat (Claude Haiku), excludes Ollama auto-configuration |

Each profile excludes the unused chat auto-configuration to prevent conflicting `ChatModel` beans.
