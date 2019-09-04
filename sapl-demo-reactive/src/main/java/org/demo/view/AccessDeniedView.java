package org.demo.view;

import org.springframework.stereotype.Component;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Component // No SpringView annotation because this view can not be navigated to
@UIScope
public class AccessDeniedView extends VerticalLayout implements View {

	public AccessDeniedView() {
		setMargin(true);
		final HorizontalLayout labelContainer = new HorizontalLayout();
		labelContainer.setSpacing(true);
		labelContainer.setWidth("100%");
		final Label lbl = new Label("You don't have access to the requested view.");
		lbl.addStyleName(ValoTheme.LABEL_FAILURE);
		lbl.setSizeUndefined();
		labelContainer.addComponent(lbl);
		labelContainer.setComponentAlignment(lbl, Alignment.MIDDLE_CENTER);
		addComponent(labelContainer);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
	}

}
