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
public class JavabasedViewView extends Div {

	public JavabasedViewView() {
		setId("javabased-view-view");

		SaplEditorConfiguration config = new SaplEditorConfiguration();
		config.setHasLineNumbers(true);
		config.setTextUpdateDelay(500);

		SaplEditor editor = new SaplEditor(config);
		editor.addDocumentChangedListener(this::onDocumentChanged);
		editor.addValidationFinishedListener(this::onValidationFinished);

		add(editor);
		editor.setDocument("policy \"set by Vaadin View after instantiation ->\\u2588<-\" permit");

		Button getValueButton = new Button();
		getValueButton.setText("Get Value");
		getValueButton.addClickListener(e -> {
			// String value = editor.GetValue();
			// System.out.println("Get Value: " + value);
		});
		add(getValueButton);

		Button setValueButton = new Button();
		setValueButton.setText("Set Value");
		setValueButton.addClickListener(e -> {
			String value = getRandomString();
			System.out.println("Set Value: " + value);
			editor.setDocument(value);
		});
		add(setValueButton);
	}

	private void onDocumentChanged(DocumentChangedEvent event) {
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
