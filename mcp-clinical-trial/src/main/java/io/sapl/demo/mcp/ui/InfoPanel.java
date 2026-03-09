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
package io.sapl.demo.mcp.ui;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.tabs.TabSheet;
import lombok.val;

import java.io.IOException;
import java.io.Serial;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

class InfoPanel extends Details {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String[] CORPUS_FILES = {
            "study_protocol.md",
            "site_heidelberg_phq9.md",
            "site_edinburgh_phq9.md",
            "adverse_events.md",
            "participant_registry.md"
    };

    private static final String[] CORPUS_LABELS = {
            "Study Protocol",
            "PHQ-9 Heidelberg",
            "PHQ-9 Edinburgh",
            "Adverse Events",
            "Participant Registry"
    };

    InfoPanel() {
        super("SMILE Study (SMILE-2025-001) - MCP Tool-Calling Demo", createTabSheet());
        setOpened(true);
        setWidthFull();
    }

    private static TabSheet createTabSheet() {
        val tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.add("Architecture", createArchitectureTab());
        for (var i = 0; i < CORPUS_FILES.length; i++) {
            tabSheet.add(CORPUS_LABELS[i], createCorpusTab(CORPUS_FILES[i]));
        }
        return tabSheet;
    }

    private static Html createArchitectureTab() {
        return new Html("""
                <div>
                <p>This demo uses <b>Spring AI tool calling</b> instead of RAG vector search.
                The LLM discovers available data via a catalog, then calls tools to fetch it.</p>
                <table style="border-collapse: collapse; width: 100%; font-size: var(--lumo-font-size-s);">
                <thead>
                <tr style="border-bottom: 2px solid var(--lumo-contrast-20pct);">
                <th style="text-align: left; padding: 4px 8px;">Tool</th>
                <th style="text-align: left; padding: 4px 8px;">Description</th>
                <th style="text-align: left; padding: 4px 8px;">Sensitivity</th>
                </tr>
                </thead>
                <tbody>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>getStudyCatalog()</code></td>
                <td style="padding: 4px 8px;">Discover available datasets and valid site names</td>
                <td style="padding: 4px 8px;">-</td>
                </tr>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>getStudyProtocol()</code></td>
                <td style="padding: 4px 8px;">Study design and methodology</td>
                <td style="padding: 4px 8px;">low</td>
                </tr>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>getPhq9Assessments(site)</code></td>
                <td style="padding: 4px 8px;">PHQ-9 scores for a specific site</td>
                <td style="padding: 4px 8px;">high</td>
                </tr>
                <tr style="border-bottom: 1px solid var(--lumo-contrast-10pct);">
                <td style="padding: 4px 8px;"><code>getAdverseEventReports(site)</code></td>
                <td style="padding: 4px 8px;">Adverse events for a specific site</td>
                <td style="padding: 4px 8px;">high</td>
                </tr>
                <tr>
                <td style="padding: 4px 8px;"><code>getParticipantRegistry()</code></td>
                <td style="padding: 4px 8px;">Participant PII (names, contacts)</td>
                <td style="padding: 4px 8px;">critical</td>
                </tr>
                </tbody>
                </table>
                <p style="font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);">
                <b>Phase 1:</b> No access control. All tools are available to all users.
                Phase 2 will add SAPL enforcement at both the catalog (discovery) and
                each tool (access) level.</p>
                </div>""");
    }

    private static Pre createCorpusTab(String filename) {
        val pre = new Pre(loadCorpusFile(filename));
        pre.getStyle().set("white-space", "pre-wrap");
        pre.getStyle().set("font-size", "var(--lumo-font-size-s)");
        pre.getStyle().set("margin", "0");
        pre.getStyle().set("max-height", "400px");
        pre.getStyle().set("overflow-y", "auto");
        return pre;
    }

    private static String loadCorpusFile(String filename) {
        val path = "corpus/" + filename;
        try (val stream = InfoPanel.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return "File not found: " + path;
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
