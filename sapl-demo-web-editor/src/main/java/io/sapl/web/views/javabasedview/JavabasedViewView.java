package io.sapl.web.views.javabasedview;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadin.SaplEditor;
import io.sapl.web.MainView;
@Route(value = "", layout = MainView.class)
@PageTitle("Java-based View")
@CssImport("styles/views/javabasedview/javabased-view-view.css")
public class JavabasedViewView extends Div {

    public JavabasedViewView() {
        setId("javabased-view-view");
        SaplEditor editor = new SaplEditor();
        add(editor);
        editor.addValueChangeListener(e -> System.out.println("value changed: "+e.isFromClient()+"  '"+e.getValue()+"'"));
        editor.setValue("policy \"set by Vaadin View after instantiation ->\\u2588<-\" permit");
    }

}
