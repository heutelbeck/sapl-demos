package io.sapl.web.views.javabasedview;

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
public class JavabasedViewView extends Div implements DocumentChangedListener, ValidationFinishedListener {

    public JavabasedViewView() {
        setId("javabased-view-view");
        
        SaplEditorConfiguration config = new SaplEditorConfiguration();
        config.setHasLineNumbers(true);
        config.setTextUpdateDelay(500);
        
        SaplEditor editor = new SaplEditor(config);
        editor.addListener((DocumentChangedListener) this);
        editor.addListener((ValidationFinishedListener) this);
        
        add(editor);
        editor.setValue("policy \"set by Vaadin View after instantiation ->\\u2588<-\" permit");
        
        Button validateButton = new Button();
        validateButton.setText("Validate Document");
        validateButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
			@Override
			public void onComponentEvent(ClickEvent<Button> event) {
				editor.validateDocument();
			}
		});
        add(validateButton);
    }
    
	@Override
	public void onDocumentChanged(DocumentChangedEvent event) {
		System.out.println("value changed: " + event.getNewValue());
	}

	@Override
	public void onValidationFinished(ValidationFinishedEvent event) {
		System.out.println("validation finished");
		Issue[] issues = event.getIssues();
		System.out.println("issue count: " + issues.length);
		for (Issue issue : issues) {
			System.out.println(issue.getDescription());
		}
	}

}
