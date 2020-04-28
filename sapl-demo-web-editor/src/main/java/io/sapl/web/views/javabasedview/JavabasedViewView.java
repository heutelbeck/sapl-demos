package io.sapl.web.views.javabasedview;

import java.nio.charset.Charset;
import java.util.Random;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
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
@PageTitle("Java-based View")
@CssImport("./styles/views/javabasedview/javabased-view-view.css")
public class JavabasedViewView extends Div implements DocumentChangedListener {

	private Button addDocumentChangedListenerButton;
	private Button removeDocumentChangedListenerButton;
	private SaplEditor editor;
	
	public JavabasedViewView() {
		setId("javabased-view-view");

		SaplEditorConfiguration config = new SaplEditorConfiguration();
		config.setHasLineNumbers(true);
		config.setTextUpdateDelay(500);

		editor = new SaplEditor(config);
		editor.addDocumentChangedListener(this);
		editor.addValidationFinishedListener(this::onValidationFinished);
		add(editor);

		Button getDocumentButton = new Button();
		getDocumentButton.setText("Get Document");
		getDocumentButton.addClickListener(e -> {
			String document = editor.getDocument();
			System.out.println("Get Document: " + document);
		});
		add(getDocumentButton);

		Button setDocumentButton = new Button();
		setDocumentButton.setText("Set Document");
		setDocumentButton.addClickListener(e -> {
			String document = getRandomString();
			System.out.println("Set Document: " + document);
			editor.setDocument(document);
		});
		add(setDocumentButton);
		
		addDocumentChangedListenerButton = new Button();
		addDocumentChangedListenerButton.setText("Add Change Listener");
		addDocumentChangedListenerButton.addClickListener(e -> {
			editor.addDocumentChangedListener(this);
			addDocumentChangedListenerButton.setEnabled(false);
			removeDocumentChangedListenerButton.setEnabled(true);
		});
		addDocumentChangedListenerButton.setEnabled(false);
		add(addDocumentChangedListenerButton);
		
		removeDocumentChangedListenerButton = new Button();
		removeDocumentChangedListenerButton.setText("Remove Change Listener");
		removeDocumentChangedListenerButton.addClickListener(e -> {
			editor.removeDocumentChangedListener(this);
			addDocumentChangedListenerButton.setEnabled(true);
			removeDocumentChangedListenerButton.setEnabled(false);
		});
		add(removeDocumentChangedListenerButton);
		
		editor.setDocument("policy \"set by Vaadin View after instantiation ->\\u2588<-\" permit");
	}

	public void onDocumentChanged(DocumentChangedEvent event) {
		System.out.println("value changed: " + event.getNewValue());
	}

	private void onValidationFinished(ValidationFinishedEvent event) {
		System.out.println("validation finished");
		Issue[] issues = event.getIssues();
		System.out.println("issue count: " + issues.length);
		for (Issue issue : issues) {
			System.out.println(issue.getDescription());
		}
	}

	private String getRandomString() {
		byte[] array = new byte[20];
		Random random = new Random();
		random.nextBytes(array);
		return new String(array, Charset.forName("UTF-8"));
	}
}
