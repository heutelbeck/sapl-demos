package io.sapl.demo.views.sapleditor;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.Issue;
import io.sapl.vaadin.SaplEditor;
import io.sapl.vaadin.SaplEditorConfiguration;
import io.sapl.vaadin.ValidationFinishedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PageTitle("SAPL Editor")
@Route(value = "", layout = MainLayout.class)
public class SAPLEditorView extends VerticalLayout {

    private static final long serialVersionUID = 8813800405531649047L;

    private static final String DEFAULT_LEFT = """
            policy "x"
            permit
            where
              subject == {"role":"author"};
            obligation
              {
                "log":"access-granted"
              }
            """;

    private static final String DEFAULT_RIGHT = """
            policy "x"
            deny
            where
              subject == {"role":"manager"};
            obligation
              {
                "log":"access-granted"
              }
            obligation
              {
                "log":"manager access-granted"
              }
            """;

    private final SaplEditor saplEditor;
    private Button toggleMerge;

    // NEW: track toggle state
    private boolean mergeEnabled = true;

    public SAPLEditorView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        final var cfg = new SaplEditorConfiguration();
        cfg.setHasLineNumbers(true);
        cfg.setTextUpdateDelay(400);
        cfg.setDarkTheme(true);

        saplEditor = new SaplEditor(cfg);
        saplEditor.setWidthFull();
        saplEditor.setHeight("70vh");

        saplEditor.addDocumentChangedListener(this::onDocumentChanged);
        saplEditor.addValidationFinishedListener(this::onValidationFinished);

        final var controls = buildControls();
        add(saplEditor, controls);

        saplEditor.setDocument(DEFAULT_LEFT);
        saplEditor.setConfigurationId("1");          // first
        saplEditor.setDocument(DEFAULT_LEFT);        // then left
        saplEditor.setMergeRightContent(DEFAULT_RIGHT);
        saplEditor.setMergeOption("showDifferences", true);
        saplEditor.setMergeOption("revertButtons", true);
        saplEditor.setMergeOption("connect", null);
        saplEditor.setMergeOption("collapseIdentical", false);
        saplEditor.setMergeOption("allowEditingOriginals", false);
        saplEditor.setMergeOption("ignoreWhitespace", false);
        // optional markers/prev/next only if the client supports them
        // initial state matches 'mergeEnabled = true'
        saplEditor.setMergeModeEnabled(mergeEnabled); // last
    }

    private HorizontalLayout buildControls() {
        final var bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.setSpacing(true);
        bar.getStyle().set("padding", "0.5rem 1rem");

        this.toggleMerge = new Button(mergeEnabled ? "Disable Merge" : "Enable Merge", this::toggleMerge);

        final var setRight = new Button("Set Right Sample", e -> saplEditor.setMergeRightContent(DEFAULT_RIGHT));
        final var clearRight = new Button("Clear Right", e -> saplEditor.setMergeRightContent(""));

        final var prev = new Button("Prev Change", e -> saplEditor.goToPreviousChange());
        final var next = new Button("Next Change", e -> saplEditor.goToNextChange());

        final var markers = new Checkbox("Change markers", true);
        markers.addValueChangeListener(e -> saplEditor.setChangeMarkersEnabled(Boolean.TRUE.equals(e.getValue())));

        final var showDiff = new Checkbox("Show differences", true);
        showDiff.addValueChangeListener(e -> saplEditor.setMergeOption("showDifferences", Boolean.TRUE.equals(e.getValue())));

        final var revertBtns = new Checkbox("Revert buttons", true);
        revertBtns.addValueChangeListener(e -> saplEditor.setMergeOption("revertButtons", Boolean.TRUE.equals(e.getValue())));

        final var connect = new ComboBox<String>("Connectors");
        connect.setItems("none", "align");
        connect.setValue("none");
        connect.addValueChangeListener(e -> saplEditor.setMergeOption("connect", "align".equals(e.getValue()) ? "align" : null));

        final var collapse = new Checkbox("Collapse identical", false);
        collapse.addValueChangeListener(e -> saplEditor.setMergeOption("collapseIdentical", Boolean.TRUE.equals(e.getValue())));

        final var allowEditOrig = new Checkbox("Allow editing right", false);
        allowEditOrig.addValueChangeListener(e -> saplEditor.setMergeOption("allowEditingOriginals", Boolean.TRUE.equals(e.getValue())));

        final var ignoreWs = new Checkbox("Ignore whitespace", false);
        ignoreWs.addValueChangeListener(e -> saplEditor.setMergeOption("ignoreWhitespace", Boolean.TRUE.equals(e.getValue())));

        final var readOnly = new Checkbox("Read-only left", false);
        readOnly.addValueChangeListener(e -> saplEditor.setReadOnly(Boolean.TRUE.equals(e.getValue())));

        final var dark = new Checkbox("Dark theme", true);
        dark.addValueChangeListener(e -> saplEditor.setDarkTheme(Boolean.TRUE.equals(e.getValue())));

        final var configId = new IntegerField("Configuration Id");
        configId.setStepButtonsVisible(true);
        configId.setMin(1);
        configId.setMax(5);
        configId.setValue(1);
        configId.addValueChangeListener(e -> {
            if (e.getValue() != null) saplEditor.setConfigurationId(e.getValue().toString());
        });

        final var setDefault = new Button("Set Doc Default", e -> saplEditor.setDocument(DEFAULT_LEFT));
        final var showDoc = new Button("Show Doc in Console", e -> log.info("SAPL: {}", saplEditor.getDocument()));

        final var filler = new FlexLayout();
        filler.setFlexGrow(1, filler);

        bar.add(toggleMerge, setRight, clearRight, prev, next,
                markers, showDiff, revertBtns, connect, collapse, allowEditOrig, ignoreWs,
                readOnly, dark, configId, setDefault, showDoc, filler);
        return bar;
    }

    private void toggleMerge(ClickEvent<Button> e) {
        mergeEnabled = !mergeEnabled;
        saplEditor.setMergeModeEnabled(mergeEnabled);
        toggleMerge.setText(mergeEnabled ? "Disable Merge" : "Enable Merge");
    }
    private void onDocumentChanged(DocumentChangedEvent event) {
        log.info("SAPL value changed: {}", event.getNewValue());
    }

    private void onValidationFinished(ValidationFinishedEvent event) {
        final Issue[] issues = event.getIssues();
        log.info("validation finished, number of issues: {}", issues.length);
        for (Issue issue : issues) {
            log.info(" - {}", issue.getDescription());
        }
    }
}
