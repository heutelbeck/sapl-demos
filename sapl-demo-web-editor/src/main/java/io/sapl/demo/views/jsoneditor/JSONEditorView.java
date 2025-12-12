// src/main/java/io/sapl/demo/views/jsoneditor/JSONEditorView.java
package io.sapl.demo.views.jsoneditor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.JsonEditor;
import io.sapl.vaadin.JsonEditorConfiguration;
import io.sapl.vaadin.ValidationStatusDisplay;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

@Slf4j
@PageTitle("JSON Editor")
@Route(value = "json", layout = MainLayout.class)
public class JSONEditorView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = -1749356040098438225L;


    private static final String LEFT_JSON_DEMO = """
{
  "meta": {
    "version": 1,
    "env": "dev",
    "generatedAt": "2025-09-18T19:00:00Z"
  },
  "settings": {
    "siteName": "SAPL Demo Portal",
    "maintenance": false,
    "maxItemsPerPage": 25,
    "retryPolicy": { "maxRetries": 3, "backoffMs": 250, "jitter": true },
    "localization": {
      "defaultLocale": "en-US",
      "supported": ["en-US", "de-DE", "fr-FR"],
      "fallbacks": { "de": "de-DE", "en": "en-US", "fr": "fr-FR" }
    }
  },
  "users": [
    {
      "id": "u-1001",
      "name": "Alice",
      "email": "alice@example.com",
      "active": true,
      "roles": ["USER", "EDITOR"],
      "attributes": {
        "department": "R&D",
        "location": "Berlin",
        "clearance": 3,
        "preferences": { "theme": "dark", "compactMode": false }
      }
    },
    {
      "id": "u-1002",
      "name": "Bob",
      "email": "bob@example.com",
      "active": true,
      "roles": ["USER"],
      "attributes": {
        "department": "Sales",
        "location": "Munich",
        "clearance": 2,
        "preferences": { "theme": "light", "compactMode": true }
      }
    },
    {
      "id": "u-1003",
      "name": "Charlie",
      "email": "charlie@example.com",
      "active": false,
      "roles": ["USER", "SUSPENDED"],
      "attributes": {
        "department": "Ops",
        "location": "Hamburg",
        "clearance": 1,
        "preferences": { "theme": "dark", "compactMode": true }
      }
    }
  ],
  "roles": [
    { "name": "USER",    "permissions": ["read:post", "read:profile"] },
    { "name": "EDITOR",  "permissions": ["read:post", "write:post", "publish:post"] },
    { "name": "ADMIN",   "permissions": ["*"] },
    { "name": "AUDITOR", "permissions": ["read:*", "export:audit"] }
  ],
  "featureFlags": {
    "newComposer": true,
    "betaPolicyHints": false,
    "fuzzySearch": true,
    "inlineDiffs": false
  },
  "experiments": [
    { "key": "exp-landing-a", "variant": "A", "traffic": 0.5 },
    { "key": "exp-landing-b", "variant": "B", "traffic": 0.5 }
  ],
  "thresholds": {
    "rateLimits": { "anonymousRps": 3, "userRps": 15, "editorRps": 25 },
    "timeouts": { "backendMs": 2000, "dbMs": 1500, "searchMs": 1800 }
  },
  "aclPolicies": [
    {
      "id": "p-001",
      "effect": "PERMIT",
      "action": ["read"],
      "resource": "post:*",
      "condition": { "expr": "subject.roles contains 'USER'" }
    },
    {
      "id": "p-002",
      "effect": "DENY",
      "action": ["publish"],
      "resource": "post:draft:*",
      "condition": { "expr": "subject.roles not contains 'EDITOR'" }
    },
    {
      "id": "p-003",
      "effect": "PERMIT",
      "action": ["export"],
      "resource": "audit:*",
      "condition": { "expr": "subject.roles contains 'AUDITOR'" }
    }
  ],
  "posts": [
    {
      "id": "post-001",
      "authorId": "u-1001",
      "title": "Hello World",
      "content": "First post content",
      "tags": ["intro", "general"],
      "status": "published",
      "views": 120,
      "sections": [
        { "h": "Intro", "t": "Welcome to the demo." },
        { "h": "Body",  "t": "This is a long paragraph explaining details..." }
      ]
    },
    {
      "id": "post-002",
      "authorId": "u-1002",
      "title": "Sales Q3 Update",
      "content": "Numbers look steady.",
      "tags": ["sales", "q3"],
      "status": "draft",
      "views": 7,
      "sections": [
        { "h": "Summary", "t": "Overview of the quarter." },
        { "h": "Risks",   "t": "Supply chain and logistics." }
      ]
    }
  ],
  "auditTrail": [
    { "ts": "2025-09-18T10:12:00Z", "user": "u-1002", "action": "LOGIN", "ip": "203.0.113.1" },
    { "ts": "2025-09-18T10:15:00Z", "user": "u-1002", "action": "EDIT_POST", "postId": "post-002" },
    { "ts": "2025-09-18T10:18:00Z", "user": "u-1001", "action": "PUBLISH_POST", "postId": "post-001" }
  ],
  "localization": {
    "strings": {
      "en-US": { "welcome": "Welcome", "logout": "Log out", "publish": "Publish", "cancel": "Cancel" },
      "de-DE": { "welcome": "Willkommen", "logout": "Abmelden", "publish": "Veröffentlichen", "cancel": "Abbrechen" }
    }
  },
  "schedules": {
    "jobs": [
      { "name": "cleanupDrafts", "cron": "0 */30 * * * *", "enabled": true },
      { "name": "reindexSearch", "cron": "0 0 */6 * * *",  "enabled": true }
    ],
    "calendar": [
      { "date": "2025-10-01", "event": "Q4 Start" },
      { "date": "2025-12-24", "event": "Maintenance Window" }
    ]
  },
  "inventory": {
    "items": [
      { "sku": "it-001", "name": "Widget A", "qty": 15, "minQty": 5, "price": 9.99, "tags": ["hw", "widget"] },
      { "sku": "it-002", "name": "Widget B", "qty": 0,  "minQty": 3, "price": 12.5, "tags": ["hw", "widget"] },
      { "sku": "it-003", "name": "Cable X",  "qty": 50, "minQty": 10, "price": 3.2,  "tags": ["hw", "cable"] },
      { "sku": "it-004", "name": "Adapter Z","qty": 8,  "minQty": 4, "price": 6.45, "tags": ["hw", "adapter"] }
    ]
  },
  "search": {
    "stopWords": ["a", "and", "the", "of", "und", "der"],
    "boost": { "title": 2.1, "content": 1.0, "tags": 1.3 }
  },
  "profiles": [
    {
      "userId": "u-1001",
      "bio": "Researcher and editor.",
      "links": [
        { "title": "Homepage", "url": "https://example.com/alice" },
        { "title": "Git",      "url": "https://git.example.com/alice" }
      ]
    },
    {
      "userId": "u-1002",
      "bio": "Sales professional.",
      "links": [
        { "title": "Homepage", "url": "https://example.com/bob" }
      ]
    }
  ],
  "schemaVersion": 7
}
""";

    private static final String RIGHT_JSON_DEMO = """
{
  "meta": {
    "version": 2,
    "env": "staging",
    "generatedAt": "2025-09-18T20:45:00Z"
  },
  "settings": {
    "siteName": "SAPL Demo Portal",
    "maintenance": true,
    "maxItemsPerPage": 50,
    "retryPolicy": { "maxRetries": 5, "backoffMs": 300, "jitter": true },
    "localization": {
      "defaultLocale": "en-US",
      "supported": ["en-US", "de-DE", "fr-FR", "es-ES"],
      "fallbacks": { "de": "de-DE", "en": "en-US", "fr": "fr-FR", "es": "es-ES" }
    }
  },
  "users": [
    {
      "id": "u-1001",
      "name": "Alice",
      "email": "alice@example.com",
      "active": true,
      "roles": ["USER", "EDITOR", "AUDITOR"],
      "attributes": {
        "department": "R&D",
        "location": "Berlin",
        "clearance": 4,
        "preferences": { "theme": "dark", "compactMode": true }
      }
    },
    {
      "id": "u-1002",
      "name": "Bob",
      "email": "bob@corp.example",
      "active": true,
      "roles": ["USER"],
      "attributes": {
        "department": "Sales",
        "location": "Munich",
        "clearance": 2,
        "preferences": { "theme": "dark", "compactMode": true }
      }
    },
    {
      "id": "u-1004",
      "name": "Dana",
      "email": "dana@example.com",
      "active": true,
      "roles": ["USER", "EDITOR"],
      "attributes": {
        "department": "R&D",
        "location": "Cologne",
        "clearance": 3,
        "preferences": { "theme": "light", "compactMode": false }
      }
    }
  ],
  "roles": [
    { "name": "USER",    "permissions": ["read:post", "read:profile"] },
    { "name": "EDITOR",  "permissions": ["read:post", "write:post", "publish:post", "archive:post"] },
    { "name": "ADMIN",   "permissions": ["*"] },
    { "name": "AUDITOR", "permissions": ["read:*", "export:audit", "read:config"] }
  ],
  "featureFlags": {
    "newComposer": false,
    "betaPolicyHints": true,
    "fuzzySearch": true,
    "inlineDiffs": true
  },
  "experiments": [
    { "key": "exp-landing-a", "variant": "B", "traffic": 0.3 },
    { "key": "exp-landing-b", "variant": "B", "traffic": 0.7 },
    { "key": "exp-new-editor", "variant": "A", "traffic": 0.1 }
  ],
  "thresholds": {
    "rateLimits": { "anonymousRps": 2, "userRps": 20, "editorRps": 30 },
    "timeouts": { "backendMs": 2500, "dbMs": 1500, "searchMs": 1600 }
  },
  "aclPolicies": [
    {
      "id": "p-001",
      "effect": "PERMIT",
      "action": ["read"],
      "resource": "post:*",
      "condition": { "expr": "subject.roles contains 'USER'" }
    },
    {
      "id": "p-002",
      "effect": "DENY",
      "action": ["publish"],
      "resource": "post:draft:*",
      "condition": { "expr": "subject.roles not contains 'EDITOR' or environment.maintenance == true" }
    },
    {
      "id": "p-004",
      "effect": "PERMIT",
      "action": ["archive"],
      "resource": "post:*",
      "condition": { "expr": "subject.roles contains 'EDITOR'" }
    }
  ],
  "posts": [
    {
      "id": "post-001",
      "authorId": "u-1001",
      "title": "Hello World",
      "content": "First post content (revised)",
      "tags": ["intro", "general", "update"],
      "status": "published",
      "views": 240,
      "sections": [
        { "h": "Intro", "t": "Welcome to the demo!" },
        { "h": "Body",  "t": "This is a longer paragraph with more details and examples..." },
        { "h": "Appendix", "t": "References and links." }
      ]
    },
    {
      "id": "post-003",
      "authorId": "u-1004",
      "title": "Experimental Editor",
      "content": "Notes on the experimental JSON editor.",
      "tags": ["editor", "experiment"],
      "status": "draft",
      "views": 0,
      "sections": [
        { "h": "Summary", "t": "Goals and scope." },
        { "h": "Roadmap", "t": "Milestones for the upcoming sprints." }
      ]
    }
  ],
  "auditTrail": [
    { "ts": "2025-09-18T10:12:00Z", "user": "u-1002", "action": "LOGIN", "ip": "203.0.113.1" },
    { "ts": "2025-09-18T11:05:00Z", "user": "u-1001", "action": "ADD_FLAG", "flag": "inlineDiffs" },
    { "ts": "2025-09-18T12:00:00Z", "user": "u-1004", "action": "CREATE_POST", "postId": "post-003" }
  ],
  "localization": {
    "strings": {
      "en-US": { "welcome": "Welcome!", "logout": "Sign out", "publish": "Publish", "cancel": "Cancel" },
      "de-DE": { "welcome": "Willkommen!", "logout": "Abmelden", "publish": "Veröffentlichen", "cancel": "Abbrechen" },
      "es-ES": { "welcome": "Bienvenido", "logout": "Salir", "publish": "Publicar", "cancel": "Cancelar" }
    }
  },
  "schedules": {
    "jobs": [
      { "name": "cleanupDrafts", "cron": "0 */20 * * * *", "enabled": true },
      { "name": "reindexSearch", "cron": "0 0 */4 * * *",  "enabled": false },
      { "name": "rotateKeys",    "cron": "0 0 3 * * *",   "enabled": true }
    ],
    "calendar": [
      { "date": "2025-10-01", "event": "Q4 Start" },
      { "date": "2025-11-11", "event": "System Upgrade" }
    ]
  },
  "inventory": {
    "items": [
      { "sku": "it-001", "name": "Widget A", "qty": 12, "minQty": 5, "price": 9.99, "tags": ["hw", "widget"] },
      { "sku": "it-002", "name": "Widget B", "qty": 4,  "minQty": 3, "price": 11.95, "tags": ["hw", "widget"] },
      { "sku": "it-004", "name": "Adapter Z","qty": 0,  "minQty": 4, "price": 6.45, "tags": ["hw", "adapter"], "discontinued": true },
      { "sku": "it-005", "name": "Dock Pro", "qty": 5,  "minQty": 2, "price": 29.0, "tags": ["hw", "dock"] }
    ]
  },
  "search": {
    "stopWords": ["a", "and", "the", "of", "und", "der", "et", "la"],
    "boost": { "title": 2.2, "content": 1.0, "tags": 1.5 }
  },
  "profiles": [
    {
      "userId": "u-1001",
      "bio": "Researcher, editor, and auditor.",
      "links": [
        { "title": "Homepage", "url": "https://example.com/alice" },
        { "title": "Git",      "url": "https://git.example.com/alice" },
        { "title": "Blog",     "url": "https://blog.example.com/alice" }
      ]
    },
    {
      "userId": "u-1004",
      "bio": "Product engineer.",
      "links": [
        { "title": "Homepage", "url": "https://example.com/dana" }
      ]
    }
  ],
  "schemaVersion": 8
}
""";

    private final JsonEditor              jsonEditor;
    private final ValidationStatusDisplay validationStatusDisplay;
    private Button                         toggleValidationDisplay;
    private boolean                        validationDisplayVisible = true;

    public JSONEditorView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        final var cfg = new JsonEditorConfiguration();
        cfg.setDarkTheme(true);
        cfg.setHasLineNumbers(true);

        jsonEditor = new JsonEditor(cfg);
        jsonEditor.addDocumentChangedListener(this::onDocumentChanged);
        jsonEditor.setWidthFull();
        jsonEditor.setHeight("70vh");

        validationStatusDisplay = new ValidationStatusDisplay();
        validationStatusDisplay.setWidthFull();

        final var controls = buildControls();

        add(jsonEditor, validationStatusDisplay, controls);

        jsonEditor.setDocument(LEFT_JSON_DEMO);
        jsonEditor.setMergeRightContent(RIGHT_JSON_DEMO);

        jsonEditor.setMergeOption("showDifferences", true);
        jsonEditor.setMergeOption("revertButtons", true);
        jsonEditor.setMergeOption("connect", null);
        jsonEditor.setMergeOption("collapseIdentical", false);
        jsonEditor.setMergeOption("allowEditingOriginals", false);
        jsonEditor.setMergeOption("ignoreWhitespace", false);

        jsonEditor.setMergeModeEnabled(true);

        // Custom diff block highlighting on by default from JS; leave as-is here.
    }

    private HorizontalLayout buildControls() {
        final var bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.setSpacing(true);
        bar.getStyle().set("padding", "0.5rem 1rem");

        final var toggleMerge = new Button("Toggle Merge", e -> jsonEditor.setMergeModeEnabled(!jsonEditor.isMergeModeEnabled()));
        toggleValidationDisplay = new Button("Hide Errors", e -> toggleValidationDisplay());

        final var setRight = new Button("Set Right to Sample", e -> jsonEditor.setMergeRightContent(RIGHT_JSON_DEMO));
        final var clearRight = new Button("Clear Right", e -> jsonEditor.setMergeRightContent("{}"));

        final var prev = new Button("Prev Change", e -> jsonEditor.goToPreviousChange());
        final var next = new Button("Next Change", e -> jsonEditor.goToNextChange());

        final var markers = new Checkbox("Change markers", true);
        markers.addValueChangeListener(e -> jsonEditor.setChangeMarkersEnabled(Boolean.TRUE.equals(e.getValue())));

        final var showDiff = new Checkbox("Show differences", true);
        showDiff.addValueChangeListener(e -> jsonEditor.setMergeOption("showDifferences", Boolean.TRUE.equals(e.getValue())));

        final var revertBtns = new Checkbox("Revert buttons", true);
        revertBtns.addValueChangeListener(e -> jsonEditor.setMergeOption("revertButtons", Boolean.TRUE.equals(e.getValue())));

        final var connect = new ComboBox<String>("Connectors");
        connect.setItems("none", "align");
        connect.setValue("none");
        connect.addValueChangeListener(e -> jsonEditor.setMergeOption("connect", "align".equals(e.getValue()) ? "align" : null));

        final var collapse = new Checkbox("Collapse identical", false);
        collapse.addValueChangeListener(e -> jsonEditor.setMergeOption("collapseIdentical", Boolean.TRUE.equals(e.getValue())));

        final var allowEditOrig = new Checkbox("Allow editing right", false);
        allowEditOrig.addValueChangeListener(e -> jsonEditor.setMergeOption("allowEditingOriginals", Boolean.TRUE.equals(e.getValue())));

        final var ignoreWs = new Checkbox("Ignore whitespace", false);
        ignoreWs.addValueChangeListener(e -> jsonEditor.setMergeOption("ignoreWhitespace", Boolean.TRUE.equals(e.getValue())));

        final var readOnly = new Checkbox("Read-only left", false);
        readOnly.addValueChangeListener(e -> jsonEditor.setReadOnly(Boolean.TRUE.equals(e.getValue())));

        final var dark = new Checkbox("Dark theme", true);
        dark.addValueChangeListener(e -> jsonEditor.setDarkTheme(Boolean.TRUE.equals(e.getValue())));

        final var filler = new FlexLayout();
        filler.setFlexGrow(1, filler);

        bar.add(toggleMerge, toggleValidationDisplay, setRight, clearRight, prev, next, markers, showDiff, revertBtns, connect, collapse, allowEditOrig, ignoreWs, readOnly, dark, filler);
        return bar;
    }

    private void onDocumentChanged(DocumentChangedEvent event) {
        log.info("JSON value changed: {}", event.getNewValue());
    }

    private void toggleValidationDisplay() {
        validationDisplayVisible = !validationDisplayVisible;
        validationStatusDisplay.setVisible(validationDisplayVisible);
        toggleValidationDisplay.setText(validationDisplayVisible ? "Hide Errors" : "Show Errors");
    }
}
