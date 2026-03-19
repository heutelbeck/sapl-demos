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

import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class AdverseEventData {

    private final Map<String, String> adverseEvents;

    AdverseEventData() {
        adverseEvents = Map.of(
                "AE-001", buildAe001(),
                "AE-002", buildAe002(),
                "AE-003", buildAe003(),
                "AE-004", buildAe004());
    }

    public String getAdverseEvent(String eventId) {
        val normalized = eventId != null ? eventId.strip().toUpperCase() : "";
        return adverseEvents.getOrDefault(normalized, "Unknown adverse event ID: " + eventId);
    }

    public String listAdverseEvents() {
        return """
                Active Adverse Events (CT-2025-001):

                | Event ID | Participant | Site       | Severity | Category           | Status          |
                |----------|-------------|------------|----------|--------------------|-----------------|
                | AE-001   | P-003       | Edinburgh  | Severe   | Suicidal ideation  | Requires action |
                | AE-002   | P-007       | Edinburgh  | Moderate | Persistent insomnia| Under review    |
                | AE-003   | P-012       | Heidelberg | Mild     | Headache           | Resolved        |
                | AE-004   | P-005       | Heidelberg | Severe   | Allergic reaction  | Requires action |""";
    }

    public String getSafetyGuidelines() {
        return """
                CT-2025-001 Safety Response Guidelines:

                1. SEVERE adverse events (required steps, execute ALL in order):
                   a) Notify the participant's emergency contact about the adverse event.
                      Use notifyParticipant with the emergency contact's name and a message
                      describing the participant's condition and required follow-up.
                   b) Notify the participant about the situation and next steps.
                   c) Suspend participant from active treatment.
                   d) Export safety report to the DSMB (Data Safety Monitoring Board).

                2. MODERATE adverse events:
                   - Contact participant within 48 hours
                   - Assess whether dose adjustment or treatment modification is needed
                   - Document in safety log and schedule follow-up

                3. MILD adverse events:
                   - Document in participant record
                   - Monitor at next scheduled visit
                   - No immediate action required unless symptoms worsen

                Emergency contact registry (must be notified for SEVERE events):
                - P-003: emergency contact Dr. James Campbell (+44 131 555 0142)
                - P-005: emergency contact Anna Mueller (+49 6221 555 0198)
                - P-007: emergency contact Mark Thompson (+44 131 555 0167)
                - P-012: emergency contact Maria Weber (+49 6221 555 0203)""";
    }

    private static String buildAe001() {
        return """
                Adverse Event AE-001:
                - Participant: P-003 (Edinburgh site)
                - Category: Suicidal ideation
                - Severity: Severe
                - Onset: 2025-02-15 (Week 6 of treatment)
                - PHQ-9 Item 9 score: 2 (more than half the days)
                - Previous PHQ-9 Item 9 scores: 0 (baseline), 0 (week 2), 1 (week 4)
                - Current treatment arm: Active (Sertraline 100mg)
                - Status: Requires immediate action
                - Notes: Participant reported increased frequency of thoughts of self-harm
                  during routine Week 6 assessment. Site investigator flagged for urgent review.""";
    }

    private static String buildAe002() {
        return """
                Adverse Event AE-002:
                - Participant: P-007 (Edinburgh site)
                - Category: Persistent insomnia
                - Severity: Moderate
                - Onset: 2025-02-01 (Week 4 of treatment)
                - Duration: Ongoing (3 weeks)
                - Current treatment arm: Active (Sertraline 50mg)
                - Status: Under review
                - Notes: Participant reports difficulty falling asleep (>60 min) and frequent
                  night wakening. Sleep quality has deteriorated since dose increase at Week 2.
                  Considering dose adjustment or adjunctive sleep medication.""";
    }

    private static String buildAe003() {
        return """
                Adverse Event AE-003:
                - Participant: P-012 (Heidelberg site)
                - Category: Headache
                - Severity: Mild
                - Onset: 2025-01-20 (Week 2 of treatment)
                - Resolution: 2025-01-25 (self-resolved)
                - Current treatment arm: Placebo
                - Status: Resolved
                - Notes: Mild tension headache reported at Week 2 visit. Resolved without
                  intervention. Likely unrelated to study treatment (placebo arm).""";
    }

    private static String buildAe004() {
        return """
                Adverse Event AE-004:
                - Participant: P-005 (Heidelberg site)
                - Category: Allergic reaction (skin rash)
                - Severity: Severe
                - Onset: 2025-02-18 (Week 6 of treatment)
                - Current treatment arm: Active (Sertraline 100mg)
                - Status: Requires immediate action
                - Notes: Participant developed widespread urticarial rash 2 hours after dose.
                  No respiratory involvement. Treatment suspended pending allergist review.
                  Possible drug hypersensitivity reaction.""";
    }

}
