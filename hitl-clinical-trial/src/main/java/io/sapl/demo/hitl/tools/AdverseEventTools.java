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

import io.sapl.demo.hitl.notification.NotificationService;
import io.sapl.spring.method.metadata.PreEnforce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdverseEventTools {

    private final AdverseEventData data;
    private final NotificationService notificationService;

    @PreEnforce(action = "'listAdverseEvents'")
    @Tool(description = "Lists all active adverse events in the SMILE study with their ID, participant, severity, and status. Call this first to discover which events need attention.")
    public String listAdverseEvents() {
        return data.listAdverseEvents();
    }

    @PreEnforce(action = "'getAdverseEvent'")
    @Tool(description = "Retrieves detailed information about a specific adverse event by its ID (e.g., AE-001). Includes participant details, severity, timeline, and clinical notes.")
    public String getAdverseEvent(@ToolParam(description = "The adverse event ID, e.g., AE-001") String eventId) {
        return data.getAdverseEvent(eventId);
    }

    @PreEnforce(action = "'getSafetyGuidelines'")
    @Tool(description = "Retrieves the SMILE study safety response guidelines, including required actions for severe, moderate, and mild adverse events, and emergency contact information.")
    public String getSafetyGuidelines() {
        return data.getSafetyGuidelines();
    }

    @PreEnforce(action = "'notifyParticipant'", resource = "{'recipient': #recipient, 'message': #message}")
    @Tool(description = "Sends a notification message to a study participant or their emergency contact. Use the recipient parameter to specify who receives the message (e.g., a participant ID like P-003 or an emergency contact name). This is a safety-critical action.")
    public String notifyParticipant(
            @ToolParam(description = "The recipient: a participant ID (e.g., P-003) or an emergency contact name") String recipient,
            @ToolParam(description = "The notification message to send") String message) {
        notificationService.send("Notified " + recipient,
                "Notification sent to " + recipient + ": " + message);
        return "Notification sent to " + recipient + ": " + message;
    }

    @PreEnforce(action = "'suspendParticipant'", resource = "{'participantId': #participantId}")
    @Tool(description = "Suspends a participant from active treatment in the study. This is a safety-critical action that halts the participant's treatment protocol.")
    public String suspendParticipant(
            @ToolParam(description = "The participant ID to suspend, e.g., P-003") String participantId) {
        notificationService.send("Suspended " + participantId,
                "Participant " + participantId + " has been suspended from active treatment. "
                        + "Treatment protocol halted. Site investigator and sponsor notified.");
        return "Participant " + participantId + " has been suspended from active treatment. "
                + "Treatment protocol halted. Site investigator and sponsor notified.";
    }

    @PreEnforce(action = "'exportSafetyReport'", resource = "{'eventId': #eventId}")
    @Tool(description = "Exports a safety report to the Data Safety Monitoring Board (DSMB). This is a safety-critical action that formally reports the adverse event for external review.")
    public String exportSafetyReport(
            @ToolParam(description = "The adverse event ID to report, e.g., AE-001") String eventId) {
        notificationService.send("Exported report for " + eventId,
                "Safety report for " + eventId + " exported to DSMB. "
                        + "Report reference: DSMB-SMILE-2025-" + eventId
                        + ". Ethics committee notification queued.");
        return "Safety report for " + eventId + " exported to DSMB. "
                + "Report reference: DSMB-SMILE-2025-" + eventId + ". Ethics committee notification queued.";
    }

}
