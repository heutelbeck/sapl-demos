package io.sapl.demo.views.jsoneditor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.demo.views.MainLayout;
import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.JsonEditor;
import io.sapl.vaadin.JsonEditorConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PageTitle("JSON Editor")
@Route(value = "json", layout = MainLayout.class)
public class JSONEditorView extends VerticalLayout {

    private static final long serialVersionUID = -1749356040098438225L;

    private static final String DEFAULT_JSON = """
            [
                {
                    _id: "post 1",
                    "author": "Bob",
                    "content": "...",
                    "page_views": 5
                },
                {
                    "_id": "post 2",
                    "author": "Bob",
                    "content": "...",
                    "page_views": 9
                },
                {
                    "_id": "post 3",
                    "author": "Bob",
                    "content": "...",
                    "page_views": 8
                }
            ]
            """;

    private final JsonEditor jsonEditor;
    private final Button     addDocumentChangedListenerButton;

    private Button removeDocumentChangedListenerButton;

    public JSONEditorView() {
        final var jsonConfig = new JsonEditorConfiguration();
        jsonConfig.setDarkTheme(true);

        jsonEditor = new JsonEditor(jsonConfig);
        jsonEditor.addDocumentChangedListener(this::onDocumentChanged);
        add(jsonEditor);

        addDocumentChangedListenerButton = new Button();
        addDocumentChangedListenerButton.setText("Add Change Listener");
        addDocumentChangedListenerButton.addClickListener(e -> {
            jsonEditor.addDocumentChangedListener(this::onDocumentChanged);
            addDocumentChangedListenerButton.setEnabled(false);
            removeDocumentChangedListenerButton.setEnabled(true);
        });
        addDocumentChangedListenerButton.setEnabled(false);
        add(addDocumentChangedListenerButton);

        removeDocumentChangedListenerButton = new Button();
        removeDocumentChangedListenerButton.setText("Remove Change Listener");
        removeDocumentChangedListenerButton.addClickListener(e -> {
            jsonEditor.removeDocumentChangedListener(this::onDocumentChanged);
            addDocumentChangedListenerButton.setEnabled(true);
            removeDocumentChangedListenerButton.setEnabled(false);
        });
        add(removeDocumentChangedListenerButton);

        Button showJsonDocumentButton = new Button();
        showJsonDocumentButton.setText("Show Document in Console");
        showJsonDocumentButton.addClickListener(e -> log.info("Current JSON value: {}", jsonEditor.getDocument()));
        add(showJsonDocumentButton);

        Button setJsonDocumentButton = new Button();
        setJsonDocumentButton.setText("Set Document to Default");
        setJsonDocumentButton.addClickListener(e -> jsonEditor.setDocument(DEFAULT_JSON));
        add(setJsonDocumentButton);

        Button toggleReadOnlyButton = new Button("Toggle ReadOnly");
        toggleReadOnlyButton.addClickListener(e -> jsonEditor.setReadOnly(!jsonEditor.isReadOnly()));
        add(toggleReadOnlyButton);

        jsonEditor.setDocument(DEFAULT_JSON);
        setSizeFull();
    }

    public void onDocumentChanged(DocumentChangedEvent event) {
        log.info("JSON value changed: {}", event.getNewValue());
    }

}
