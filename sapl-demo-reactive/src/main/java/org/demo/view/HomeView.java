package org.demo.view;

import org.demo.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.spring.PolicyEnforcementPoint;

@SpringView(name = "") // Root view
public class HomeView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	private transient PolicyEnforcementPoint pep;

	@Autowired
	public HomeView(PolicyEnforcementPoint pep) {
		this.pep = pep;

		setMargin(true);
		setSpacing(true);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final String username = SecurityUtils.getUsername();
		final Label label = new Label("Welcome " + username + "!");
		label.setStyleName(ValoTheme.LABEL_LARGE);
		addComponent(label);

		final GridLayout grid = new GridLayout(1, 5);
		grid.setSpacing(true);
		grid.setHideEmptyRowsAndColumns(false);
		addComponent(grid);

		final Button traditionalBtn = new Button(
				"Show Patient List (blocking, single requests)",
				click -> getUI().getNavigator().navigateTo("traditional"));
		traditionalBtn.setData("ui:view:home:showPatientListTraditionalButton");
		traditionalBtn.setWidth("360px");
		grid.addComponent(traditionalBtn, 0, 0);

		final Button multiRequestBtn = new Button(
				"Show Patient List (blocking, multi-request)",
				click -> getUI().getNavigator().navigateTo("multiRequest"));
		multiRequestBtn.setData("ui:view:home:showPatientListMultiRequestButton");
		multiRequestBtn.setWidth("360px");
		grid.addComponent(multiRequestBtn, 0, 1);

		final Button reactiveBtn = new Button("Show Reactive View (single requests)",
				click -> getUI().getNavigator().navigateTo("reactive"));
		reactiveBtn.setData("ui:view:home:showReactiveViewButton");
		reactiveBtn.setWidth("360px");
		grid.addComponent(reactiveBtn, 0, 3);

		final Button reactiveMultiRequestBtn = new Button(
				"Show Reactive View (multi-request)",
				click -> getUI().getNavigator().navigateTo("reactiveMultiRequest"));
		reactiveMultiRequestBtn
				.setData("ui:view:home:showReactiveViewMultiRequestButton");
		reactiveMultiRequestBtn.setWidth("360px");
		grid.addComponent(reactiveMultiRequestBtn, 0, 4);

		final Authentication authentication = SecurityUtils.getAuthentication();
		final MultiRequest multiRequest = new MultiRequest()
				.addRequest("useTraditionalBtn", authentication, "use",
						traditionalBtn.getData())
				.addRequest("useMultiRequestBtn", authentication, "use",
						multiRequestBtn.getData())
				.addRequest("useReactiveBtn", authentication, "use",
						reactiveBtn.getData())
				.addRequest("useReactiveMultiRequestBtn", authentication, "use",
						reactiveMultiRequestBtn.getData());

		final MultiResponse multiResponse = pep.filterEnforce(multiRequest).blockFirst();

		if (multiResponse == null) {
			throw new IllegalStateException("PEP returned a null multi-response");
		}

		traditionalBtn.setEnabled(
				multiResponse.isAccessPermittedForRequestWithId("useTraditionalBtn"));
		multiRequestBtn.setEnabled(
				multiResponse.isAccessPermittedForRequestWithId("useMultiRequestBtn"));
		reactiveBtn.setEnabled(
				multiResponse.isAccessPermittedForRequestWithId("useReactiveBtn"));
		reactiveMultiRequestBtn.setEnabled(multiResponse
				.isAccessPermittedForRequestWithId("useReactiveMultiRequestBtn"));
	}

}
