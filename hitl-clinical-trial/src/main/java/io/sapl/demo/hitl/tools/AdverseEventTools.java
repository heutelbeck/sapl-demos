/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.demo.hitl.tools;

import java.util.Map;

import io.sapl.api.model.UndefinedValue;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.demo.hitl.approval.ApprovalService;
import io.sapl.demo.hitl.approval.SessionIdHolder;
import io.sapl.demo.hitl.domain.DemoPrincipal;
import io.sapl.demo.hitl.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdverseEventTools {

    static final String ERROR_ACTION_DENIED = "Action denied by operator. The tool call was not executed.";

    private final AdverseEventData data;
    private final PolicyDecisionPoint pdp;
    private final ApprovalService approvalService;
    private final NotificationService notificationService;

    @Tool(description = "Lists all active adverse events in the SMILE study with their ID, participant, severity, and status. Call this first to discover which events need attention.")
    public String listAdverseEvents() {
        log.info("Tool executing: listAdverseEvents");
        return data.listAdverseEvents();
    }

    @Tool(description = "Retrieves detailed information about a specific adverse event by its ID (e.g., AE-001). Includes participant details, severity, timeline, and clinical notes.")
    public String getAdverseEvent(@ToolParam(description = "The adverse event ID, e.g., AE-001") String eventId) {
        log.info("Tool executing: getAdverseEvent(eventId={})", eventId);
        return data.getAdverseEvent(eventId);
    }

    @Tool(description = "Retrieves the SMILE study safety response guidelines, including required actions for severe, moderate, and mild adverse events, and emergency contact information.")
    public String getSafetyGuidelines() {
        log.info("Tool executing: getSafetyGuidelines");
        return data.getSafetyGuidelines();
    }

    @Tool(description = "Sends a notification message to a study participant or their emergency contact. Use the recipient parameter to specify who receives the message (e.g., a participant ID like P-003 or an emergency contact name). This is a safety-critical action.")
    public String notifyParticipant(
            @ToolParam(description = "The recipient: a participant ID (e.g., P-003) or an emergency contact name") String recipient,
            @ToolParam(description = "The notification message to send") String message) {
        if (!checkPolicy("notifyParticipant", Map.of("recipient", recipient, "message", message))) {
            return ERROR_ACTION_DENIED;
        }
        if (!requireApproval("notifyParticipant", "Notify " + recipient, "Message: " + message)) {
            return ERROR_ACTION_DENIED;
        }
        log.info("ACTION: notifyParticipant(recipient={}, message={})", recipient, message);
        notificationService.send("Notified " + recipient,
                "Notification sent to " + recipient + ": " + message);
        return "Notification sent to " + recipient + ": " + message;
    }

    @Tool(description = "Suspends a participant from active treatment in the study. This is a safety-critical action that halts the participant's treatment protocol.")
    public String suspendParticipant(
            @ToolParam(description = "The participant ID to suspend, e.g., P-003") String participantId) {
        if (!checkPolicy("suspendParticipant", Map.of("participantId", participantId))) {
            return ERROR_ACTION_DENIED;
        }
        if (!requireApproval("suspendParticipant", "Suspend participant " + participantId,
                "Participant " + participantId + " will be suspended from active treatment.")) {
            return ERROR_ACTION_DENIED;
        }
        log.info("ACTION: suspendParticipant(participantId={})", participantId);
        notificationService.send("Suspended " + participantId,
                "Participant " + participantId + " has been suspended from active treatment. "
                        + "Treatment protocol halted. Site investigator and sponsor notified.");
        return "Participant " + participantId + " has been suspended from active treatment. "
                + "Treatment protocol halted. Site investigator and sponsor notified.";
    }

    @Tool(description = "Exports a safety report to the Data Safety Monitoring Board (DSMB). This is a safety-critical action that formally reports the adverse event for external review.")
    public String exportSafetyReport(
            @ToolParam(description = "The adverse event ID to report, e.g., AE-001") String eventId) {
        if (!checkPolicy("exportSafetyReport", Map.of("eventId", eventId))) {
            return ERROR_ACTION_DENIED;
        }
        if (!requireApproval("exportSafetyReport", "Export safety report for " + eventId,
                "Safety report for " + eventId + " will be exported to the DSMB for external review.")) {
            return ERROR_ACTION_DENIED;
        }
        log.info("ACTION: exportSafetyReport(eventId={})", eventId);
        notificationService.send("Exported report for " + eventId,
                "Safety report for " + eventId + " exported to DSMB. "
                        + "Report reference: DSMB-SMILE-2025-" + eventId
                        + ". Ethics committee notification queued.");
        return "Safety report for " + eventId + " exported to DSMB. "
                + "Report reference: DSMB-SMILE-2025-" + eventId + ". Ethics committee notification queued.";
    }

    private boolean checkPolicy(String toolName, Object resource) {
        val auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof DemoPrincipal principal)) {
            return false;
        }
        val subscription = AuthorizationSubscription.of(principal, toolName, resource);
        log.info("Authorization subscription: {}", subscription);
        val decision = pdp.decideOnce(subscription).block();
        if (decision == null || decision.decision() != Decision.PERMIT
                || !decision.obligations().isEmpty()
                || !(decision.resource() instanceof UndefinedValue)) {
            log.info("PDP denied access to tool '{}': {}", toolName, decision);
            return false;
        }
        log.info("PDP permitted access to tool '{}'", toolName);
        return true;
    }

    private boolean requireApproval(String toolName, String summary, String detail) {
        val sessionId = SessionIdHolder.get();
        if (sessionId == null) {
            return false;
        }
        return approvalService.requestApproval(sessionId, toolName, summary, detail, false);
    }

}
