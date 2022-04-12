package io.sapl.vaadindemo.shared;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utilities {
    public static Component getDefaultHeader(String pageName) {
        var header = new HorizontalLayout();

        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.BASELINE);

        var h2 = new H2(pageName);
        header.add(h2);
        header.expand(h2);

        return header;
    }

    public static HorizontalLayout getInfoText(String infoText) {
        Pre infoTextPre = new Pre(infoText);
        infoTextPre.getStyle()
                .set("white-space", "pre-wrap")
                .set("margin", "auto")
                .set("font-size", "medium")
                .set("padding-left", "5px")
                .set("padding-right", "5px");
        Icon infoIcon = new Icon(VaadinIcon.INFO_CIRCLE);

        return new HorizontalLayout(infoIcon, infoTextPre);
    }

    public static void setEmptyLayout(VerticalLayout layout) {
        layout.setWidth(null);
        layout.setHeightFull();
        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        layout.add(img);
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.getStyle().set("text-align", "center");
    }
}
