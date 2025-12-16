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
            requirement "Policy with simple PIP should permit read for subject.<test.upper> equal to WILLI" {
                given
                    - policy "policyWithSimplePIP"
            
                scenario "willi tries to read something with simple attribute mocking"
                given
                    // mocks the PIP to return "WILLI" when "test.upper" is called
                    - attribute "test.upper" emits "WILLI"
                when "willi" attempts "read" on "something"
                expect permit;
            
                scenario "willi tries to read something with specific parent value attribute mocking"
                given
                    // specify the parent value in "<>" that is expected
                    - attribute "test.upper" of <"willi"> emits "WILLI"
                when "willi" attempts "read" on "something"
                expect permit;
            
                scenario "willi tries to read something with PIP returning error"
                given
                    // specify the parent value in "<>" that is expected and return an error
                    - attribute "test.upper" of <"willi"> emits error("something is wrong")
                when "willi" attempts "read" on "something"
                expect indeterminate;
                }
            """;

    private static final String COMPARE_TEST = """
            
            requirement "Policy with complex PIP should only permit read when pip.attributeWithParams emits true" {
                given
                    - attribute "a.b" emits 123
                    - policy "policyWithComplexPIP"
            
                scenario "willi tries to read something with specific parent value and mixed parameter matchers attribute mocking"
                given
                    //attribute 1 and attribute 2 are used by pip.attributeWithParams
                    - attribute "pip.attribute1" emits 1
                    - attribute "pip.attribute2" emits 2
                    // parent value is always true for this case, parameters are the return of attribute 1 and attribute 2
                    // allows the same matching logic as functions do, and exact JSON value, any or a JsonNodeMatcher, see https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-test#jsonnodematcher for all possible matcher definitions
                    - attribute "pip.attributeWithParams" of <true>(matching number 1, any) emits true
                when "willi" attempts "read" on "something"
                expect permit;
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
