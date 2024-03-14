package io.sapl.demo.views.sapleditor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    private static final String DEFAULT_POLICY = "policy \"set by Vaadin View after instantiation ->\\u2588<-\" permit";

    private final Button     addDocumentChangedListenerButton;
    private final Button     addValidationChangedListenerButton;
    private final SaplEditor saplEditor;

    private Button removeDocumentChangedListenerButton;
    private Button removeValidationChangedListenerButton;

    public SAPLEditorView() {
        var saplConfig = new SaplEditorConfiguration();
        saplConfig.setHasLineNumbers(true);
        saplConfig.setTextUpdateDelay(500);
        saplConfig.setDarkTheme(true);

        saplEditor = new SaplEditor(saplConfig);
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
        setDocumentToDefaultButton.addClickListener(e -> saplEditor.setDocument(DEFAULT_POLICY));
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

        saplEditor.setDocument(DEFAULT_POLICY);
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
