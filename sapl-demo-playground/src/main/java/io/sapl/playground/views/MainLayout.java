package io.sapl.playground.views;

import java.util.HashMap;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import io.sapl.playground.examples.BasicExample;
import io.sapl.playground.examples.Example;
import io.sapl.playground.examples.SchemaExample;
import io.sapl.playground.examples.SpringDataExample;
import io.sapl.playground.examples.SpringSecurityExample;

public class MainLayout extends AppLayout {

    private final ExampleSelectedViewBus exampleSelectedViewBus;
    private HashMap<String, Example>     examples;

    public MainLayout(ExampleSelectedViewBus exampleSelectedViewBus) {
        this.exampleSelectedViewBus = exampleSelectedViewBus;
        initializeExamples();
        addToNavbar(createHeaderContent());

    }

    private Component createHeaderContent() {
        var header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        var layout = new HorizontalLayout();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.SMALL);
        layout.getStyle().set("max-height", "55px");

        var appName = new H1("SAPL Playground");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.XLARGE, Margin.AUTO);

        var linkToDocs = new Anchor("https://sapl.io", "SAPL Homepage");

        layout.add(logo(), appName, linkToDocs, exampleSelector());

        header.add(layout);
        return header;
    }

    private Component logo() {
        var logo = new Image("images/logo-header.png", "SAPL Logo");
        logo.addClassNames(Margin.LARGE);
        logo.setHeight(44, Unit.PIXELS);
        return logo;
    }

    private Component exampleSelector() {
        Select<String> select = new Select<>();
        select.setPlaceholder("Examples");
        select.setItems(this.examples.keySet());
        select.setId("dropdownButton");
        select.addValueChangeListener(event -> this.exampleSelectedViewBus.getContentView()
                .setExample(this.examples.get(event.getValue()), true));
        return select;
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

        example = new SchemaExample();
        this.examples.put(example.getDisplayName(), example);
    }

}
