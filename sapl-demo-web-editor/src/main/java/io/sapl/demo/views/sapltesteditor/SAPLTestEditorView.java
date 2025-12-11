package io.sapl.demo.views.sapltesteditor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.*;

import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

@Slf4j
@PageTitle("SAPL Test Editor")
@Route(value = "sapltesteditor", layout = MainLayout.class)
public class SAPLTestEditorView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = -2637310974422530266L;

    private static final String DEFAULT_TEST = """
            requirement "Policy Simple should grant read access for willi on something" {
                scenario "willi tries to read something"
                given
                    - policy "policySimple"
                when subject "willi" attempts action "read" on resource "something"
                expect permit;

                scenario "not_willi tries to read something"
                given
                    - policy "policySimple"
                when "not_willi" attempts "read" on "something"
                expect deny;
            }""";

    private static final String DEFAULT_RIGHT = """
            requirement "Policy Simple should grant read access for willi on something" {
                scenario "willi tries to read something"
                given
                    - policy "policySimple"
                when subject "willi" attempts action "read" on resource "something"
                expect permit;

                scenario "manager tries to read something"
                given
                    - policy "policySimple"
                when "manager" attempts "read" on "something"
                expect deny;
            }""";

    private final SaplTestEditor editor;
    private Button toggleMergeBtn;
    private boolean mergeEnabled = false;

    public SAPLTestEditorView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        var cfg = new SaplTestEditorConfiguration();
        cfg.setHasLineNumbers(true);
        cfg.setTextUpdateDelay(500);
        cfg.setDarkTheme(true);

        editor = new SaplTestEditor(cfg);
        editor.setWidthFull();
        editor.setHeight("70vh");

        editor.addDocumentChangedListener(this::onDocumentChanged);
        editor.addValidationFinishedListener(this::onValidationFinished);

        add(editor, buildControls());

        // initial content + merge setup
        editor.setDocument(DEFAULT_TEST);
        editor.setMergeRightContent(DEFAULT_RIGHT);
        editor.setMergeOption("showDifferences", true);
        editor.setMergeOption("revertButtons", true);
        editor.setMergeOption("connect", null);
        editor.setMergeOption("collapseIdentical", false);
        editor.setMergeOption("allowEditingOriginals", false);
        editor.setMergeOption("ignoreWhitespace", false);
        editor.setMergeModeEnabled(mergeEnabled);
    }

    private HorizontalLayout buildControls() {
        var bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.setSpacing(true);

        toggleMergeBtn = new Button(mergeEnabled ? "Disable Merge" : "Enable Merge", e -> toggleMerge());

        var setRight = new Button("Set Right Sample", e -> editor.setMergeRightContent(DEFAULT_RIGHT));
        var clearRight = new Button("Clear Right", e -> editor.setMergeRightContent(""));

        var prev = new Button("Prev Change", e -> editor.goToPreviousChange());
        var next = new Button("Next Change", e -> editor.goToNextChange());

        var showDiff = new Checkbox("Show differences", true);
        showDiff.addValueChangeListener(e -> editor.setMergeOption("showDifferences", Boolean.TRUE.equals(e.getValue())));

        var revertBtns = new Checkbox("Revert buttons", true);
        revertBtns.addValueChangeListener(e -> editor.setMergeOption("revertButtons", Boolean.TRUE.equals(e.getValue())));

        var connect = new ComboBox<String>("Connectors");
        connect.setItems("none", "align");
        connect.setValue("none");
        connect.addValueChangeListener(e -> editor.setMergeOption("connect", "align".equals(e.getValue()) ? "align" : null));

        var collapse = new Checkbox("Collapse identical", false);
        collapse.addValueChangeListener(e -> editor.setMergeOption("collapseIdentical", Boolean.TRUE.equals(e.getValue())));

        var allowEditOrig = new Checkbox("Allow editing right", false);
        allowEditOrig.addValueChangeListener(e -> editor.setMergeOption("allowEditingOriginals", Boolean.TRUE.equals(e.getValue())));

        var ignoreWs = new Checkbox("Ignore whitespace", false);
        ignoreWs.addValueChangeListener(e -> editor.setMergeOption("ignoreWhitespace", Boolean.TRUE.equals(e.getValue())));

        var readOnly = new Checkbox("Read-only (left)", false);
        readOnly.addValueChangeListener(e -> editor.setReadOnly(Boolean.TRUE.equals(e.getValue())));

        var dark = new Checkbox("Dark theme", true);
        dark.addValueChangeListener(e -> editor.setDarkTheme(Boolean.TRUE.equals(e.getValue())));

        var setDefault = new Button("Set Doc Default", e -> editor.setDocument(DEFAULT_TEST));
        var showDoc = new Button("Show Doc in Console", e -> log.info("SAPL-TEST: {}", editor.getDocument()));

        var filler = new FlexLayout();
        filler.setFlexGrow(1, filler);

        bar.add(
                toggleMergeBtn, setRight, clearRight,
                prev, next,
                showDiff, revertBtns, connect, collapse, allowEditOrig, ignoreWs,
                readOnly, dark, setDefault, showDoc,
                filler
        );
        return bar;
    }

    private void toggleMerge() {
        mergeEnabled = !mergeEnabled;
        editor.setMergeModeEnabled(mergeEnabled);
        toggleMergeBtn.setText(mergeEnabled ? "Disable Merge" : "Enable Merge");
    }

    private void onDocumentChanged(DocumentChangedEvent event) {
        log.info("SAPL-TEST value changed: {}", event.getNewValue());
    }

    private void onValidationFinished(ValidationFinishedEvent event) {
        Issue[] issues = event.getIssues();
        log.info("validation finished, number of issues: {}", issues.length);
        for (Issue issue : issues) {
            log.info(" - {}", issue.getDescription());
        }
    }
}
