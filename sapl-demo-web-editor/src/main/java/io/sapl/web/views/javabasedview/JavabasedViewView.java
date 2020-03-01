package io.sapl.web.views.javabasedview;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadin.SaplEditor;
import io.sapl.vaadin.SaplEditorConfiguration;
import io.sapl.web.MainView;
@Route(value = "", layout = MainView.class)
@PageTitle("Java-based View")
@CssImport("styles/views/javabasedview/javabased-view-view.css")
public class JavabasedViewView extends Div {

    public JavabasedViewView() {
        setId("javabased-view-view");
        
        SaplEditorConfiguration config = new SaplEditorConfiguration();
        config.HasLineNumbers = false;
        config.TextUpdateDelay = 2000;
        
        SaplEditor editor = new SaplEditor(config);
        
        editor.addDocumentChangedListener(new SaplEditor.DocumentChangedListener() {
			@Override
			public void onDocumentChanged(String newValue) {
				System.out.println("value changed: " + newValue);
			}
		});
        
        add(editor);
        editor.setValue("policy \"set by Vaadin View after instantiation ->\\u2588<-\" permit");
    }

}
