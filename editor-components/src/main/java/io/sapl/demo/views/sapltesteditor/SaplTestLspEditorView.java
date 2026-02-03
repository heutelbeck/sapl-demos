/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.demo.views.sapltesteditor;

import java.io.Serial;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.ValidationFinishedEvent;
import io.sapl.vaadin.ValidationStatusDisplay;
import io.sapl.vaadin.lsp.SaplEditorLspConfiguration;
import io.sapl.vaadin.lsp.SaplEditorLspConfiguration.AutocompleteTrigger;
import io.sapl.vaadin.lsp.SaplTestEditorLsp;
import lombok.extern.slf4j.Slf4j;

/**
 * Demo view for the LSP-based SAPLTest editor using CodeMirror 6.
 * This view demonstrates testing SAPL policies with the test DSL.
 */
@Slf4j
@PageTitle("SAPL Test Editor")
@Route(value = "test", layout = MainLayout.class)
public class SaplTestLspEditorView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_TEST = """
            /*
             * Integration Test Demo
             *
             * This file demonstrates integration testing with the SAPL test language:
             * - Integration tests with multiple documents
             * - Combining algorithm specification
             * - Variables definition
             * - Environment configuration
             * - All documents loading (omitting document specification)
             *
             * Integration tests evaluate policies against a PDP configuration
             * rather than a single document unit test.
             */
            
            requirement "should grant read access for WILLI on foo depending on policy set and PDP Config" {
            
                scenario "WILLI tries to read foo using explicit document list"
                    given
                        // List specific documents for integration test
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        // Specify combining algorithm explicitly
                        - priority permit or abstain errors propagate
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "WILLI tries to read foo with deny-overrides"
                    given
                        // Same documents but different algorithm
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - priority deny or deny errors propagate
                    when "WILLI" attempts "read" on "foo"
                    expect deny;
            
                scenario "WILLI tries to read foo with only-one-applicable"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - unique or abstain errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // Multiple policies may apply, so indeterminate
                    expect indeterminate;
            
                scenario "WILLI tries to read foo with deny-unless-permit"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - priority permit or deny errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // At least one permit -> permit
                    expect permit;
            
                scenario "WILLI tries to read foo with permit-unless-deny"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - priority deny or permit errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // Policy A denies, so deny
                    expect deny;
            }
            
            requirement "Integration tests with variables" {
            
                scenario "test with empty variables"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                        // Override PDP variables with empty object
                        - variables {}
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "test with custom variables"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                        // Define custom variables for test
                        - variables {
                            "maxAttempts": 3,
                            "debugMode": true,
                            "allowedRoles": ["admin", "user"]
                        }
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "test with variables and environment"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        // Use permit-overrides so policy_B's permit wins over policy_A's deny
                        - priority permit or permit errors propagate
                        - variables { "testVar": "value" }
                    when "WILLI" attempts "read" on "foo" in { "tenant": "acme", "region": "eu-west" }
                    expect permit;
            }
            
            requirement "Integration test with all combining algorithms" {
            
                // Central given for all scenarios
                given
                    - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
            
                scenario "deny-overrides algorithm"
                    given
                        - priority deny or deny errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // If any policy denies, result is deny
                    expect deny;
            
                scenario "permit-overrides algorithm"
                    given
                        - priority permit or permit errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // If any policy permits, result is permit
                    expect permit;
            
                scenario "only-one-applicable algorithm"
                    given
                        - unique or abstain errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // Multiple applicable policies -> indeterminate
                    expect indeterminate;
            
                scenario "deny-unless-permit algorithm"
                    given
                        - priority permit or deny errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // At least one permit exists -> permit
                    expect permit;
            
                scenario "permit-unless-deny algorithm"
                    given
                        - priority deny or permit errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // At least one deny exists -> deny
                    expect deny;
            }
            
            requirement "Integration test with mocking" {
            
                scenario "integration test with function mock"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                        // Can still mock functions in integration tests
                        - function some.helper() maps to true
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "integration test with attribute mock"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        // Use permit-overrides so policy_B's permit wins
                        - priority permit or permit errors propagate
                        // Demonstrate that attributes can be mocked in integration tests
                        - attribute "timeMock" <time.now> emits "2025-01-06T12:00:00Z"
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "integration test with multiple documents"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // policy_A denies, policy_B permits, permit-overrides means permit wins
                    expect permit;
            
                scenario "streaming with policy that uses time attribute"
                    given
                        // policy_C uses <time.now(interval)> with parameter
                        // It checks: time.secondOf(<time.now(interval)>) >= 4
                        - documents "policiesIT/policy_C"
                        - priority permit or abstain errors propagate
                        // Mock the time attribute with parameter using any matcher
                        - attribute "timeMock" <time.now(any)> emits "time1"
                        // Mock secondOf to return controlled values based on time values
                        - function time.secondOf("time1") maps to 5
                        - function time.secondOf("time2") maps to 2
                    when "WILLI" attempts "read" on "bar"
                    // With secondOf returning 5 (>= 4), policy_C permits
                    expect permit
                    then
                        // Change time to trigger re-evaluation
                        - attribute "timeMock" emits "time2"
                    // With secondOf returning 2 (< 4), policy_C's where clause fails
                    expect not-applicable;
            }
            
            requirement "Minimal integration test syntax" {
            
                // When no given block is provided at all, uses all documents
                // with default combining algorithm (deny-overrides by default)
                scenario "using all documents with defaults"
                    when "WILLI" attempts "read" on "foo"
                    // With deny-overrides default and policy_A denying "foo", result is deny
                    expect deny;
            
            }
            """;

    private static final String COMPARE_TEST = """
            /*
             * Integration Test Demo
             *
             * This file demonstrates integration testing with the SAPL test language:
             * - All documents loading (omitting document specification)
             *
             * Integration tests evaluate policies against a PDP configuration
             * rather than a single document unit test.
             */
            
            requirement "should grant read access for WILLI on foo depending on policy set and PDP Config" {
            
                scenario "WILLI tries to read foo using explicit document list"
                    given
                        // List specific documents for integration test
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        // Specify combining algorithm explicitly
                        - priority permit or abstain errors propagate
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "WILLI tries to read foo with deny-overrides"
                    given
                        // Same documents but different algorithm
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - priority deny or deny errors propagate
                    when "WILLI" attempts "read" on "foo"
                    expect deny;
            
                scenario "WILLI tries to read foo with only-one-applicable"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - unique or abstain errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // Multiple policies may apply, so indeterminate
                    expect indeterminate;
            
                scenario "WILLI tries to read foo with deny-unless-permit"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - priority permit or deny errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // At least one permit -> permit
                    expect permit;
            
                scenario "WILLI tries to read foo with permit-unless-deny"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
                        - priority deny or permit errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // Policy A denies, so deny
                    expect deny;
            }
            
            requirement "Integration tests with variables" {
            
                scenario "test with empty variables"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                        // Override PDP variables with empty object
                        - variables {}
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "test with custom variables"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                        // Define custom variables for test
                        - variables {
                            "maxAttempts": 3,
                            "debugMode": true,
                            "allowedRoles": ["admin", "user"]
                        }
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "test with variables and environment"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        // Use permit-overrides so policy_B's permit wins over policy_A's deny
                        - priority permit or permit errors propagate
                        - variables { "testVar": "value" }
                    when "WILLI" attempts "read" on "foo" in { "tenant": "acme", "region": "eu-west" }
                    expect permit;
            }
            
            requirement "Integration test with all combining algorithms" {
            
                // Central given for all scenarios
                given
                    - documents "policiesIT/policy_A", "policiesIT/policy_B", "policiesIT/policy_C"
            
                scenario "deny-overrides algorithm"
                    given
                        - priority deny or deny errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // If any policy denies, result is deny
                    expect deny;
            
                scenario "permit-overrides algorithm"
                    given
                        - priority permit or permit errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // If any policy permits, result is permit
                    expect permit;
            
                scenario "only-one-applicable algorithm"
                    given
                        - unique or abstain errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // Multiple applicable policies -> indeterminate
                    expect indeterminate;
            
                scenario "deny-unless-permit algorithm"
                    given
                        - priority permit or deny errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // At least one permit exists -> permit
                    expect permit;
            
                scenario "permit-unless-deny algorithm"
                    given
                        - priority deny or permit errors abstain
                    when "WILLI" attempts "read" on "foo"
                    // At least one deny exists -> deny
                    expect deny;
            }
            
            requirement "Integration test with mocking" {
            
                scenario "integration test with function mock"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                        // Can still mock functions in integration tests
                        - function some.helper() maps to true
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "integration test with attribute mock"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        // Use permit-overrides so policy_B's permit wins
                        - priority permit or permit errors propagate
                        // Demonstrate that attributes can be mocked in integration tests
                        - attribute "timeMock" <time.now> emits "2025-01-06T12:00:00Z"
                    when "WILLI" attempts "read" on "foo"
                    expect permit;
            
                scenario "integration test with multiple documents"
                    given
                        - documents "policiesIT/policy_A", "policiesIT/policy_B"
                        - priority permit or permit errors propagate
                    when "WILLI" attempts "read" on "foo"
                    // policy_A denies, policy_B permits, permit-overrides means permit wins
                    expect permit;
            
                scenario "streaming with policy that uses time attribute"
                    given
                        - documents "policiesIT/policy_C"
                        - priority permit or abstain errors propagate
                        // Mock the time attribute with parameter using any matcher
                        - attribute "timeMock" <time.now(any)> emits "time1"
                        - function time.secondOf("time1") maps to 5
                        - function time.secondOf("time2") maps to 2
                    when "WILLI" attempts "read" on "bar"
                    // With secondOf returning 5 (>= 4), policy_C permits
                    expect permit
                    then
                        // Change time to trigger re-evaluation
                        - attribute "timeMock" emits "time2"
                    // With secondOf returning 2 (< 4), policy_C's where clause fails
                    expect not-applicable;
            }
            
            requirement "Minimal integration test syntax" {
            
                // When no given block is provided at all, uses all documents
                // with default combining algorithm (deny-overrides by default)
                scenario "using all documents with defaults"
                    when "WILLI" attempts "read" on "foo"
                    // With deny-overrides default and policy_A denying "foo", result is deny
                    expect deny;
            
            }
            
            """;

    private final SaplTestEditorLsp     editor;
    private final ValidationStatusDisplay validationDisplay;

    public SaplTestLspEditorView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        var config = new SaplEditorLspConfiguration();
        config.setDarkTheme(true);
        config.setHasLineNumbers(true);
        config.setWsUrl(getWsUrl());

        editor = new SaplTestEditorLsp(config);
        editor.setWidthFull();
        editor.setHeight("70vh");

        editor.addDocumentChangedListener(this::onDocumentChanged);
        editor.addValidationFinishedListener(this::onValidationFinished);

        validationDisplay = new ValidationStatusDisplay();
        validationDisplay.setWidthFull();

        add(editor, validationDisplay, buildControls());

        // Load default content after component is attached to ensure proper initialization
        editor.addAttachListener(event -> editor.setDocument(DEFAULT_TEST));
    }

    private String getWsUrl() {
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

        var setDefault = new Button("Reset to Default", e -> editor.setDocument(DEFAULT_TEST));
        var showDoc = new Button("Log Document", e -> log.info("Document:\n{}", editor.getDocument()));

        var configId = new TextField("Configuration ID");
        configId.setPlaceholder("e.g., test-suite-1");
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
                editor.setMergeRightContent(COMPARE_TEST);
            }
            editor.setMergeModeEnabled(enabled);
            highlightChanges.setEnabled(enabled);
            collapseUnchanged.setEnabled(enabled);
            prevChange.setEnabled(enabled);
            nextChange.setEnabled(enabled);
            syncScroll.setEnabled(enabled);
            showGutter.setEnabled(enabled);
        });

        controls.add(topRow, middleRow, bottomRow);
        return controls;
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
