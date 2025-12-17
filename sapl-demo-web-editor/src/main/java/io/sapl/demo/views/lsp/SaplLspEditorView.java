/*
 * Copyright (C) 2017-2025 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.demo.views.lsp;

import java.io.Serial;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.api.coverage.PolicyCoverageData;
import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.ValidationFinishedEvent;
import io.sapl.vaadin.ValidationStatusDisplay;
import io.sapl.vaadin.lsp.SaplEditorLsp;
import io.sapl.vaadin.lsp.SaplEditorLspConfiguration;
import io.sapl.vaadin.lsp.SaplEditorLspConfiguration.AutocompleteTrigger;
import lombok.extern.slf4j.Slf4j;

/**
 * Demo view for the new LSP-based SAPL editor using CodeMirror 6.
 * This view demonstrates the CM6 + LSP integration as an alternative
 * to the legacy Xtext-based editor.
 */
@Slf4j
@PageTitle("SAPL Editor")
@Route(value = "", layout = MainLayout.class)
public class SaplLspEditorView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_POLICY = """
            /*
             * Document Access Control Policy
             * Version 2.0 - Enhanced security
             */
            policy "document-access-control"
            permit
                action == "read"
            where
                var userClearance = subject.<user.clearanceLevel>;
                var docClassification = resource.<document.classification>;
                userClearance >= docClassification;
                subject.department == "security";
            obligation
                {
                    "type": "audit",
                    "action": "logAccess",
                    "user": subject.id,
                    "document": resource.id
                }
            advice
                {
                    "type": "notification",
                    "message": "Classified document access logged"
                }
            """;

    private static final String COMPARE_POLICY = """
            /*
             * Document Access Control Policy
             * Version 1.0 - Original
             */
            policy "document-access-control"
            permit
                action == "read"
            where
                var userClearance = subject.<user.clearanceLevel>;
                var docClassification = resource.<document.classification>;
                userClearance >= docClassification;
            obligation
                {
                    "type": "audit",
                    "action": "logAccess",
                    "user": subject.id
                }
            """;

    private final SaplEditorLsp         editor;
    private final ValidationStatusDisplay validationDisplay;

    public SaplLspEditorView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        var config = new SaplEditorLspConfiguration();
        config.setLanguage("sapl");
        config.setDarkTheme(true);
        config.setHasLineNumbers(true);
        // WebSocket URL for LSP - relative to current host
        config.setWsUrl(getWsUrl());

        editor = new SaplEditorLsp(config);
        editor.setWidthFull();
        editor.setHeight("70vh");

        editor.addDocumentChangedListener(this::onDocumentChanged);
        editor.addValidationFinishedListener(this::onValidationFinished);

        validationDisplay = new ValidationStatusDisplay();
        validationDisplay.setWidthFull();

        add(editor, validationDisplay, buildControls());

        // Load default content after component is attached to ensure proper initialization
        editor.addAttachListener(event -> editor.setDocument(DEFAULT_POLICY));
    }

    private String getWsUrl() {
        // Construct WebSocket URL based on current page location
        // This will be evaluated client-side, so we use a placeholder
        // that the JavaScript can resolve
        return "ws://localhost:8080/sapl-lsp";
    }

    private VerticalLayout buildControls() {
        var controls = new VerticalLayout();
        controls.setWidthFull();
        controls.setPadding(false);
        controls.setSpacing(true);

        var topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setAlignItems(Alignment.CENTER);
        topRow.setSpacing(true);

        var setDefault = new Button("Reset to Default", e -> editor.setDocument(DEFAULT_POLICY));
        var showDoc = new Button("Log Document", e -> log.info("Document:\n{}", editor.getDocument()));

        var configId = new TextField("Configuration ID");
        configId.setPlaceholder("e.g., production, staging");
        configId.setWidth("200px");
        configId.addValueChangeListener(e -> {
            var value = e.getValue();
            editor.setConfigurationId(value != null && !value.isBlank() ? value : null);
            log.info("Configuration ID set to: {}", value);
        });

        var dark = new Checkbox("Dark theme", true);
        dark.addValueChangeListener(e -> editor.setDarkTheme(Boolean.TRUE.equals(e.getValue())));

        var readOnly = new Checkbox("Read-only", false);
        readOnly.addValueChangeListener(e -> editor.setReadOnly(Boolean.TRUE.equals(e.getValue())));

        var autoComplete = new Checkbox("Auto-complete on typing", false);
        autoComplete.addValueChangeListener(e -> {
            var onTyping = Boolean.TRUE.equals(e.getValue());
            editor.setAutocompleteTrigger(onTyping ? AutocompleteTrigger.ON_TYPING : AutocompleteTrigger.MANUAL);
        });

        topRow.add(setDefault, showDoc, configId, dark, readOnly, autoComplete);

        var middleRow = new HorizontalLayout();
        middleRow.setWidthFull();
        middleRow.setAlignItems(Alignment.CENTER);
        middleRow.setSpacing(true);

        var matchBrackets = new Checkbox("Match brackets", true);
        matchBrackets.addValueChangeListener(e -> editor.setMatchBrackets(Boolean.TRUE.equals(e.getValue())));

        var autoCloseBrackets = new Checkbox("Auto-close brackets", true);
        autoCloseBrackets.addValueChangeListener(e -> editor.setAutoCloseBrackets(Boolean.TRUE.equals(e.getValue())));

        var mergeMode = new Checkbox("Merge mode", false);

        var highlightChanges = new Checkbox("Highlight changes", true);
        highlightChanges.setEnabled(false);
        highlightChanges.addValueChangeListener(e -> editor.setHighlightChanges(Boolean.TRUE.equals(e.getValue())));

        var collapseUnchanged = new Checkbox("Collapse unchanged", false);
        collapseUnchanged.setEnabled(false);
        collapseUnchanged.addValueChangeListener(e -> editor.setCollapseUnchanged(Boolean.TRUE.equals(e.getValue())));

        middleRow.add(matchBrackets, autoCloseBrackets, mergeMode, highlightChanges, collapseUnchanged);

        var bottomRow = new HorizontalLayout();
        bottomRow.setWidthFull();
        bottomRow.setAlignItems(Alignment.CENTER);
        bottomRow.setSpacing(true);

        var prevChange = new Button("Previous Change", e -> editor.goToPreviousChange());
        prevChange.setEnabled(false);

        var nextChange = new Button("Next Change", e -> editor.goToNextChange());
        nextChange.setEnabled(false);

        var syncScroll = new Checkbox("Sync scroll", false);
        syncScroll.setEnabled(false);
        syncScroll.addValueChangeListener(e -> editor.setSyncScroll(Boolean.TRUE.equals(e.getValue())));

        var showGutter = new Checkbox("Show gutter", true);
        showGutter.setEnabled(false);
        showGutter.addValueChangeListener(e -> editor.setGutter(Boolean.TRUE.equals(e.getValue())));

        bottomRow.add(prevChange, nextChange, syncScroll, showGutter);

        mergeMode.addValueChangeListener(e -> {
            var enabled = Boolean.TRUE.equals(e.getValue());
            if (enabled) {
                // Set content BEFORE enabling merge mode so MergeView has both documents
                editor.setMergeRightContent(COMPARE_POLICY);
            }
            editor.setMergeModeEnabled(enabled);
            highlightChanges.setEnabled(enabled);
            collapseUnchanged.setEnabled(enabled);
            prevChange.setEnabled(enabled);
            nextChange.setEnabled(enabled);
            syncScroll.setEnabled(enabled);
            showGutter.setEnabled(enabled);
        });

        var coverageRow = new HorizontalLayout();
        coverageRow.setWidthFull();
        coverageRow.setAlignItems(Alignment.CENTER);
        coverageRow.setSpacing(true);

        var showCoverage = new Button("Show Sample Coverage", e -> applySampleCoverage());
        var clearCoverage = new Button("Clear Coverage", e -> editor.clearCoverage());

        coverageRow.add(showCoverage, clearCoverage);

        controls.add(topRow, middleRow, bottomRow, coverageRow);
        return controls;
    }

    private void applySampleCoverage() {
        var coverage = new PolicyCoverageData("document-access-control", DEFAULT_POLICY, "policy");
        coverage.recordTargetHit(true, 7, 8);
        coverage.recordConditionHit(0, 10, 10, 0, 0, true);
        coverage.recordConditionHit(0, 10, 10, 0, 0, true);
        coverage.recordConditionHit(0, 10, 10, 0, 0, false);
        coverage.recordConditionHit(1, 11, 11, 0, 0, true);
        coverage.recordConditionHit(1, 11, 11, 0, 0, false);
        coverage.recordConditionHit(2, 12, 12, 0, 0, true);
        coverage.recordConditionHit(3, 13, 13, 0, 0, false);
        editor.setCoverage(coverage);
        log.info("Applied sample coverage for policy '{}'", coverage.getDocumentName());
    }

    private void onDocumentChanged(DocumentChangedEvent event) {
        log.debug("Document changed (from client: {})", event.getFromClient());
    }

    private void onValidationFinished(ValidationFinishedEvent event) {
        var issues = event.getIssues();
        log.info("LSP validation: {} issues", issues.length);
        for (var issue : issues) {
            log.debug("  - {}", issue.getDescription());
        }
        validationDisplay.setIssues(issues);
    }
}
