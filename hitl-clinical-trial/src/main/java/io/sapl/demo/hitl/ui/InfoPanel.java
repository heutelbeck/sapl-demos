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
package io.sapl.demo.hitl.ui;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.tabs.TabSheet;
import io.sapl.demo.hitl.tools.AdverseEventData;
import lombok.val;

import java.io.Serial;

class InfoPanel extends Details {

    @Serial
    private static final long serialVersionUID = 1L;

    InfoPanel(AdverseEventData data) {
        super("SMILE Study (SMILE-2025-001) - Human-in-the-Loop Demo", createTabSheet(data));
        setOpened(true);
        setWidthFull();
    }

    private static TabSheet createTabSheet(AdverseEventData data) {
        val tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.add("Scenario", createScenarioTab());
        tabSheet.add("Architecture", createArchitectureTab());
        tabSheet.add("Event List", createPreTab(data.listAdverseEvents()));
        tabSheet.add("AE-001", createPreTab(data.getAdverseEvent("AE-001")));
        tabSheet.add("AE-002", createPreTab(data.getAdverseEvent("AE-002")));
        tabSheet.add("AE-003", createPreTab(data.getAdverseEvent("AE-003")));
        tabSheet.add("AE-004", createPreTab(data.getAdverseEvent("AE-004")));
        tabSheet.add("Safety Guidelines", createPreTab(data.getSafetyGuidelines()));
        return tabSheet;
    }

    private static Pre createPreTab(String content) {
        val pre = new Pre(content);
        pre.getStyle().set("white-space", "pre-wrap");
        pre.getStyle().set("font-size", "var(--lumo-font-size-s)");
        pre.getStyle().set("margin", "0");
        pre.getStyle().set("max-height", "400px");
        pre.getStyle().set("overflow-y", "auto");
        return pre;
    }

    private static Html createScenarioTab() {
        return new Html("""
                <div>
                <p>This demo shows <b>human-in-the-loop (HITL) approval workflows</b> for AI tool calls.
                An AI assistant helps a safety officer respond to adverse events in a clinical trial.
                Read-only tools (list events, view details, read guidelines) execute freely, but
                <b>safety-critical actions</b> (notify participant, suspend treatment, export report)
                require explicit human approval before execution.</p>
                <p>The user acts as <b>Dr. Elena Fischer</b> (Safety Officer). When the AI agent
                attempts to execute an action tool, a SAPL obligation triggers an approval dialog.
                The user can approve or decline each action individually.</p>
                <p><b>Auto-Approve:</b> Toggle to automatically approve all action tool calls
                (for demo convenience). When disabled, each action tool requires manual approval
                via a dialog.</p>
                </div>""");
    }

    private static Html createArchitectureTab() {
        return new Html("""
                <div>
                <p>This demo uses <b>Spring AI tool calling</b> with SAPL obligation-based approval.
                The LLM calls tools to view and act on adverse event data.</p>
                <table style="border-collapse: collapse; width: 100%; font-size: var(--lumo-font-size-s);">
                <thead>
                <tr style="border-bottom: 2px solid var(--lumo-contrast-20pct);">
                <th style="text-align: left; padding: 4px 8px;">Tool</th>
                <th style="text-align: left; padding: 4px 8px;">Description</th>
                <th style="text-align: left; padding: 4px 8px;">Approval</th>
                </tr>
                </thead>
                <tbody>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>listAdverseEvents()</code></td>
                <td style="padding: 4px 8px;">List all active adverse events</td>
                <td style="padding: 4px 8px;">None</td>
                </tr>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>getAdverseEvent(id)</code></td>
                <td style="padding: 4px 8px;">Get details for a specific event</td>
                <td style="padding: 4px 8px;">None</td>
                </tr>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>getSafetyGuidelines()</code></td>
                <td style="padding: 4px 8px;">Study safety response guidelines</td>
                <td style="padding: 4px 8px;">None</td>
                </tr>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>notifyParticipant(id, msg)</code></td>
                <td style="padding: 4px 8px;">Send notification to participant</td>
                <td style="padding: 4px 8px;">Human approval</td>
                </tr>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>suspendParticipant(id)</code></td>
                <td style="padding: 4px 8px;">Suspend participant from treatment</td>
                <td style="padding: 4px 8px;">Human approval</td>
                </tr>
                <tr>
                <td style="padding: 4px 8px;"><code>exportSafetyReport(id)</code></td>
                <td style="padding: 4px 8px;">Export report to DSMB</td>
                <td style="padding: 4px 8px;">Human approval</td>
                </tr>
                </tbody>
                </table>
                <p style="font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);">
                SAPL policies emit a <code>PERMIT</code> with an <b>obligation</b> requiring human approval
                for action tools. The constraint handler blocks until the user approves or declines.
                Declining fails the obligation, resulting in access denial.</p>
                </div>""");
    }

}
