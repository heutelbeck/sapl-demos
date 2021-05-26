package io.sapl.playground.views.main;

import java.util.Arrays;

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

import io.sapl.playground.models.ExamplesEnum;

/**
 * The main view is a top-level placeholder for other views.
 */
@SuppressWarnings("serial")
@CssImport(value = "./styles/views/main/main-view.css", themeFor = "vaadin-app-layout")
@CssImport("./styles/views/main/main-view.css")
//@PWA(name = "SAPL Playground", shortName = "SAPL", enableInstallPrompt = false)
@JsModule("./styles/shared-styles.js")
public class MainView extends AppLayout {

    public MainView() {
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
        
        Image logo = new Image("images/logo-header.png", "SAPL Logo");
        logo.setId("logo");
        header.add(logo);
        
        header.add(new H1("SAPL Playground"));
        
        //VerticalLayout vertical = new VerticalLayout();
        //vertical.setAlignItems(Alignment.END);
        
        Div buttons = new Div();
        buttons.setClassName("alignRight");
        
        Anchor linkToDocs = new Anchor("https://sapl.io/docs/sapl-reference.html", "Docs");
        linkToDocs.setId("linkToDocsButton");
        buttons.add(linkToDocs);
        
        Select<String> select = new Select<>();
        select.setPlaceholder("Examples");
        select.setItems(Arrays.asList(ExamplesEnum.values()).stream().map(e -> e.getDisplayValue()));
        select.setId("dropdownButton");
        buttons.add(select);
        
        header.add(buttons);
        
        return header;
    }
}
