package io.sapl.web.views.javabasedview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.DocumentChangedListener;
import io.sapl.vaadin.Issue;
import io.sapl.vaadin.SaplEditor;
import io.sapl.vaadin.SaplEditorConfiguration;
import io.sapl.vaadin.ValidationFinishedEvent;
import io.sapl.vaadin.ValidationFinishedListener;
import io.sapl.web.MainView;

@Route(value = "", layout = MainView.class)
@PageTitle("Sapl Editor Demo")
@CssImport("./styles/views/javabasedview/javabased-view-view.css")
@SuppressWarnings("serial")
public class SaplEditorView extends Div implements DocumentChangedListener, ValidationFinishedListener {

	private Button addDocumentChangedListenerButton;
	private Button removeDocumentChangedListenerButton;
	private Button addValidationChangedListenerButton;
	private Button removeValidationChangedListenerButton;
	private SaplEditor saplEditor;

	public SaplEditorView() {
		final String DefaultSaplString = "policy \"set by Vaadin View after instantiation ->\\u2588<-\" permit";

		setId("sapl-editor-view");

		SaplEditorConfiguration saplConfig = new SaplEditorConfiguration();
		saplConfig.setHasLineNumbers(true);
		saplConfig.setTextUpdateDelay(500);

		saplEditor = new SaplEditor(saplConfig);
		saplEditor.addDocumentChangedListener(this);
		saplEditor.addValidationFinishedListener(this);
		add(saplEditor);

		addDocumentChangedListenerButton = new Button();
		addDocumentChangedListenerButton.setText("Add Change Listener");
		addDocumentChangedListenerButton.addClickListener(e -> {
			saplEditor.addDocumentChangedListener(this);
			addDocumentChangedListenerButton.setEnabled(false);
			removeDocumentChangedListenerButton.setEnabled(true);
		});
		addDocumentChangedListenerButton.setEnabled(false);
		add(addDocumentChangedListenerButton);

		removeDocumentChangedListenerButton = new Button();
		removeDocumentChangedListenerButton.setText("Remove Change Listener");
		removeDocumentChangedListenerButton.addClickListener(e -> {
			saplEditor.removeDocumentChangedListener(this);
			addDocumentChangedListenerButton.setEnabled(true);
			removeDocumentChangedListenerButton.setEnabled(false);
		});
		add(removeDocumentChangedListenerButton);

		addValidationChangedListenerButton = new Button();
		addValidationChangedListenerButton.setText("Add Validation Listener");
		addValidationChangedListenerButton.addClickListener(e -> {
			saplEditor.addValidationFinishedListener(this);
			addValidationChangedListenerButton.setEnabled(false);
			removeValidationChangedListenerButton.setEnabled(true);
		});
		addValidationChangedListenerButton.setEnabled(false);
		add(addValidationChangedListenerButton);

		removeValidationChangedListenerButton = new Button();
		removeValidationChangedListenerButton.setText("Remove Validation Listener");
		removeValidationChangedListenerButton.addClickListener(e -> {
			saplEditor.removeValidationFinishedListener(this);
			addValidationChangedListenerButton.setEnabled(true);
			removeValidationChangedListenerButton.setEnabled(false);
		});
		add(removeValidationChangedListenerButton);

		Button setDocumentToDefaultButton = new Button();
		setDocumentToDefaultButton.setText("Set Document to Default");
		setDocumentToDefaultButton.addClickListener(e -> {
			saplEditor.setDocument(DefaultSaplString);
		});
		add(setDocumentToDefaultButton);

		Button showSaplDocumentButton = new Button();
		showSaplDocumentButton.setText("Show Document in Console");
		showSaplDocumentButton.addClickListener(e -> {
			String document = saplEditor.getDocument();
			System.out.println("Current SAPL value: " + document);
		});
		add(showSaplDocumentButton);

		Button toggleReadOnlyButton = new Button();
		toggleReadOnlyButton.setText("Toggle ReadOnly");
		toggleReadOnlyButton.addClickListener(e -> {
			saplEditor.setReadOnly(!saplEditor.isReadOnly());
		});
		add(toggleReadOnlyButton);

		saplEditor.setDocument(DefaultSaplString);
	}

	public void onDocumentChanged(DocumentChangedEvent event) {
		System.out.println("value changed: " + event.getNewValue());
	}

	public void onValidationFinished(ValidationFinishedEvent event) {
		System.out.println("validation finished");
		Issue[] issues = event.getIssues();
		System.out.println("issue count: " + issues.length);
		for (Issue issue : issues) {
			System.out.println(issue.getDescription());
		}
	}
}
