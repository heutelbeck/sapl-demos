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
package io.sapl.demo.views.jsoneditor;

import java.io.Serial;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.lsp.JsonEditor;
import io.sapl.vaadin.lsp.JsonEditorConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * Demo view for the JSON editor with syntax highlighting, linting,
 * and merge view for comparing documents.
 */
@Slf4j
@PageTitle("JSON Editor")
@Route(value = "json", layout = MainLayout.class)
public class JsonEditorView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_JSON = """
            {
              "subject": {
                "id": "alice",
                "roles": ["admin", "auditor"],
                "department": "security",
                "clearanceLevel": 5
              },
              "action": {
                "type": "read",
                "target": "document"
              },
              "resource": {
                "id": "doc-12345",
                "classification": "confidential",
                "owner": "bob"
              },
              "environment": {
                "timestamp": "2025-01-15T10:30:00Z",
                "location": "headquarters",
                "ipAddress": "192.168.1.100"
              }
            }
            """;

    private static final String COMPARE_JSON = """
            {
              "subject": {
                "id": "alice",
                "roles": ["user"],
                "department": "marketing"
              },
              "action": {
                "type": "write",
                "target": "document"
              },
              "resource": {
                "id": "doc-12345",
                "classification": "public",
                "owner": "bob"
              },
              "environment": {
                "timestamp": "2025-01-15T10:30:00Z",
                "location": "remote-office",
                "ipAddress": "10.0.0.55"
              }
            }
            """;

    private final JsonEditor editor;

    public JsonEditorView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        var config = new JsonEditorConfiguration();
        config.setDarkTheme(true);
        config.setHasLineNumbers(true);

        editor = new JsonEditor(config);
        editor.setWidthFull();
        editor.setHeight("70vh");
        editor.setDocument(DEFAULT_JSON);

        add(editor, buildControls());
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

        var setDefault = new Button("Reset to Default", e -> editor.setDocument(DEFAULT_JSON));
        var showDoc = new Button("Log Document", e -> log.info("Document:\n{}", editor.getDocument()));

        var dark = new Checkbox("Dark theme", true);
        dark.addValueChangeListener(e -> editor.setDarkTheme(Boolean.TRUE.equals(e.getValue())));

        var readOnly = new Checkbox("Read-only", false);
        readOnly.addValueChangeListener(e -> editor.setReadOnly(Boolean.TRUE.equals(e.getValue())));

        var lint = new Checkbox("Lint", true);
        lint.addValueChangeListener(e -> editor.setLint(Boolean.TRUE.equals(e.getValue())));

        topRow.add(setDefault, showDoc, dark, readOnly, lint);

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
                editor.setMergeRightContent(COMPARE_JSON);
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
}
