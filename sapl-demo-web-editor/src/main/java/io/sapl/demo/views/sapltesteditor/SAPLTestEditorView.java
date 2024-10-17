package io.sapl.demo.views.sapltesteditor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.Issue;
import io.sapl.vaadin.SaplTestEditor;
import io.sapl.vaadin.SaplTestEditorConfiguration;
import io.sapl.vaadin.ValidationFinishedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PageTitle("SAPL Test Editor")
@Route(value = "sapltesteditor", layout = MainLayout.class)
public class SAPLTestEditorView extends VerticalLayout {

    private static final long serialVersionUID = -2637310974422530266L;

    private static final String DEFAULT_TEST = """
            requirement "Policy Simple should grant read access for willi on something" {
                scenario "willi tries to read something" //define a scenario with a specific name. This scenario represents a single isolated test case within the requirment. A requirment can contain 1:n scenario definitions. The scenario name needs to be unique within a requirement.
                given //marks the start of the "given" section which is used to define the policy under test (and a lot more which is shown in the other examples)
                    - policy "policySimple" //define the name of the policy to test, has to be located in src/main/resources (or in a folder structure below this folder)
                when subject "willi" attempts action "read" on resource "something" //defines the AuthorizationSubscription that should be used for the test
                expect permit; // defines the expected AuthorizationDecision to compare against the actual outcome. scenario is finished with an ";"

                scenario "not_willi tries to read something" //second scenario for negative test
                given
                    - policy "policySimple"
                when "not_willi" attempts "read" on "something" //short form to define the AuthorizationSubscription omitting the keywords "subject", "action" and "resource"
                expect deny; // expect deny here since the policy only permits read access for "willi" and not for "not_willi"
            }""";

    private final Button         addDocumentChangedListenerButton;
    private final Button         addValidationChangedListenerButton;
    private final SaplTestEditor saplEditor;

    private Button removeDocumentChangedListenerButton;
    private Button removeValidationChangedListenerButton;

    public SAPLTestEditorView() {
        final var saplConfig = new SaplTestEditorConfiguration();
        saplConfig.setHasLineNumbers(true);
        saplConfig.setTextUpdateDelay(500);
        saplConfig.setDarkTheme(true);

        saplEditor = new SaplTestEditor(saplConfig);
        saplEditor.addDocumentChangedListener(this::onDocumentChanged);
        saplEditor.addValidationFinishedListener(this::onValidationFinished);
        add(saplEditor);

        addDocumentChangedListenerButton = new Button();
        addDocumentChangedListenerButton.setText("Add Change Listener");
        addDocumentChangedListenerButton.addClickListener(e -> {
            saplEditor.addDocumentChangedListener(this::onDocumentChanged);
            addDocumentChangedListenerButton.setEnabled(false);
            removeDocumentChangedListenerButton.setEnabled(true);
        });
        addDocumentChangedListenerButton.setEnabled(false);
        add(addDocumentChangedListenerButton);

        removeDocumentChangedListenerButton = new Button();
        removeDocumentChangedListenerButton.setText("Remove Change Listener");
        removeDocumentChangedListenerButton.addClickListener(e -> {
            saplEditor.removeDocumentChangedListener(this::onDocumentChanged);
            addDocumentChangedListenerButton.setEnabled(true);
            removeDocumentChangedListenerButton.setEnabled(false);
        });
        add(removeDocumentChangedListenerButton);

        addValidationChangedListenerButton = new Button();
        addValidationChangedListenerButton.setText("Add Validation Listener");
        addValidationChangedListenerButton.addClickListener(e -> {
            saplEditor.addValidationFinishedListener(this::onValidationFinished);
            addValidationChangedListenerButton.setEnabled(false);
            removeValidationChangedListenerButton.setEnabled(true);
        });
        addValidationChangedListenerButton.setEnabled(false);
        add(addValidationChangedListenerButton);

        removeValidationChangedListenerButton = new Button();
        removeValidationChangedListenerButton.setText("Remove Validation Listener");
        removeValidationChangedListenerButton.addClickListener(e -> {
            saplEditor.removeValidationFinishedListener(this::onValidationFinished);
            addValidationChangedListenerButton.setEnabled(true);
            removeValidationChangedListenerButton.setEnabled(false);
        });
        add(removeValidationChangedListenerButton);

        Button setDocumentToDefaultButton = new Button();
        setDocumentToDefaultButton.setText("Set Document to Default");
        setDocumentToDefaultButton.addClickListener(e -> saplEditor.setDocument(DEFAULT_TEST));
        add(setDocumentToDefaultButton);

        Button showSaplDocumentButton = new Button();
        showSaplDocumentButton.setText("Show Document in Console");
        showSaplDocumentButton.addClickListener(e -> {
            String document = saplEditor.getDocument();
            log.info("Current SAPL value: {}", document);
        });
        add(showSaplDocumentButton);

        Button toggleReadOnlyButton = new Button();
        toggleReadOnlyButton.setText("Toggle ReadOnly");
        toggleReadOnlyButton.addClickListener(e -> saplEditor.setReadOnly(!saplEditor.isReadOnly()));
        add(toggleReadOnlyButton);

        saplEditor.setDocument(DEFAULT_TEST);
        setSizeFull();
    }

    public void onDocumentChanged(DocumentChangedEvent event) {
        log.info("value changed: {}", event.getNewValue());
    }

    public void onValidationFinished(ValidationFinishedEvent event) {
        Issue[] issues = event.getIssues();
        log.info("validation finished, number of issues: {}", issues.length);
        for (Issue issue : issues) {
            log.info(" - {} " + issue.getDescription());
        }
    }
}
