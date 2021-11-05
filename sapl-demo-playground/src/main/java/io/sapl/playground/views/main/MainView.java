package io.sapl.playground.views.main;

import java.util.HashMap;
import java.util.Map;

import io.sapl.playground.examples.BasicExample;
import io.sapl.playground.examples.Example;
import io.sapl.playground.examples.SpringDataExample;
import io.sapl.playground.examples.SpringSecurityExample;
import io.sapl.playground.views.ExampleSelectedViewBus;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.server.PWA;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport(value = "./styles/views/main/main-view.css", themeFor = "vaadin-app-layout")
@CssImport("./styles/views/main/main-view.css")
@PWA(name = "SAPL Playground", shortName = "SAPL", enableInstallPrompt = false, iconPath = "images/favicon.png")
@JsModule("./styles/shared-styles.js")
public class MainView extends AppLayout {

	private final ExampleSelectedViewBus exampleSelectedViewBus;
	private Map<String, Example> examples;
	
    public MainView(ExampleSelectedViewBus exampleSelectedViewBus) {
        this.exampleSelectedViewBus = exampleSelectedViewBus;
        initializeExamples();
    	HorizontalLayout header = createHeader();
        addToNavbar(header);
    }
 
    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.setWidthFull();        
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setId("header");
        
        headerSetLogo(header);
        
        headerAddTitle(header);
        
        headerAddButtons(header);
        
        return header;
    }

	private void headerAddButtons(HorizontalLayout header) {
		Div buttons = new Div();
        buttons.setClassName("alignRight");
        
        Anchor linkToDocs = new Anchor("https://sapl.io/docs/sapl-reference.html", "Docs");
        linkToDocs.setId("linkToDocsButton");
        buttons.add(linkToDocs);
        
        Select<String> select = new Select<>();
        select.setPlaceholder("Examples");
        select.setItems(this.examples.keySet());
        select.setId("dropdownButton");
        select.addValueChangeListener(
                event -> this.exampleSelectedViewBus.getContentView().setExample(this.examples.get(event.getValue()), true));
        buttons.add(select);
        
        
        header.add(buttons);
	}

	private void headerAddTitle(HorizontalLayout header) {
		header.add(new H1("SAPL Playground"));
	}

	private void headerSetLogo(HorizontalLayout header) {
		Image logo = new Image("images/logo-header.png", "SAPL Logo");
        logo.setId("logo");
        header.add(logo);
	}
    
    private void initializeExamples() {
        this.examples = new HashMap<>();
        
    	Example example;
    	
    	example = new BasicExample();
    	this.examples.put(example.getDisplayName(), example);
    	
    	example = new SpringSecurityExample();
    	this.examples.put(example.getDisplayName(), example);
    	
    	example = new SpringDataExample();
    	this.examples.put(example.getDisplayName(), example);
    }
}
