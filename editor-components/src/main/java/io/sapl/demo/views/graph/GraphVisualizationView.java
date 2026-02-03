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
package io.sapl.demo.views.graph;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.lsp.graph.JsonGraphVisualization;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

/**
 * Demo view for the JSON graph visualization component.
 * Displays JSON data as an interactive hierarchical tree using D3.js.
 */
@Slf4j
@PageTitle("Graph Visualization")
@Route(value = "graph", layout = MainLayout.class)
public class GraphVisualizationView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String AUTH_REQUEST_JSON = """
            {
              "subject": {
                "id": "alice",
                "roles": ["admin", "auditor"],
                "department": "security",
                "clearanceLevel": 5
              },
              "action": {
                "name": "read",
                "parameters": {
                  "format": "pdf",
                  "includeSensitive": true
                }
              },
              "resource": {
                "type": "document",
                "id": "doc-12345",
                "classification": "restricted",
                "owner": "bob",
                "tags": ["financial", "quarterly-report"]
              },
              "environment": {
                "time": "2025-01-15T10:30:00Z",
                "location": {
                  "building": "headquarters",
                  "floor": 3,
                  "room": "secure-area"
                },
                "network": {
                  "internal": true,
                  "ipAddress": "192.168.1.100"
                }
              }
            }
            """;

    private static final String AUTH_DECISION_JSON = """
            {
              "decision": "PERMIT",
              "obligations": [
                {
                  "type": "logging",
                  "action": "audit",
                  "message": "Document access granted to admin user"
                },
                {
                  "type": "notification",
                  "recipients": ["security@company.com"],
                  "template": "sensitive-access"
                }
              ],
              "advice": [
                {
                  "type": "display",
                  "message": "This document is classified as restricted"
                }
              ],
              "resource": {
                "filtered": true,
                "redactedFields": ["internalNotes", "costCenter"]
              }
            }
            """;

    private final JsonGraphVisualization graph;

    public GraphVisualizationView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        graph = new JsonGraphVisualization();
        graph.setWidthFull();
        graph.setHeight("70vh");
        graph.setJsonData(AUTH_REQUEST_JSON);

        add(graph, buildControls());
    }

    private HorizontalLayout buildControls() {
        var bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.setSpacing(true);

        var showRequest = new Button("Authorization Request", e -> {
            log.info("Showing authorization request");
            graph.setJsonData(AUTH_REQUEST_JSON);
        });

        var showDecision = new Button("Authorization Decision", e -> {
            log.info("Showing authorization decision");
            graph.setJsonData(AUTH_DECISION_JSON);
        });

        var filler = new FlexLayout();
        filler.setFlexGrow(1, filler);

        bar.add(showRequest, showDecision, filler);
        return bar;
    }
}
