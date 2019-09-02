package org.demo.view;

import org.demo.security.SecurityUtils;
import org.demo.view.traditional.multirequest.MultiRequestStreamManager;
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

@SpringView(name = "") // Root view
public class HomeView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	private static final String BUTTON_WIDTH = "475px";

	public HomeView() {
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

		final Button traditionalBtn = new Button("Show Patient View (session based, single requests)",
				click -> getUI().getNavigator().navigateTo("traditional"));
		traditionalBtn.setData("ui:view:home:showPatientListTraditionalButton");
		traditionalBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(traditionalBtn, 0, 0);

		final Button multiRequestBtn = new Button("Show Patient View (session based, multi-request)",
				click -> getUI().getNavigator().navigateTo("multiRequest"));
		multiRequestBtn.setData("ui:view:home:showPatientListMultiRequestButton");
		multiRequestBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(multiRequestBtn, 0, 1);

		final Button reactiveBtn = new Button("Show Live-Data View (reactive frontend, single requests)",
				click -> getUI().getNavigator().navigateTo("reactive"));
		reactiveBtn.setData("ui:view:home:showReactiveViewButton");
		reactiveBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(reactiveBtn, 0, 3);

		final Button reactiveMultiRequestBtn = new Button("Show Live-Data View (reactive frontend, multi-request)",
				click -> getUI().getNavigator().navigateTo("reactiveMultiRequest"));
		reactiveMultiRequestBtn.setData("ui:view:home:showReactiveViewMultiRequestButton");
		reactiveMultiRequestBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(reactiveMultiRequestBtn, 0, 4);

		final MultiRequestStreamManager streamManager = getSession().getAttribute(MultiRequestStreamManager.class);
		if (!streamManager.hasMultiRequestSubscriptionFor("homeViewButtons")) {
			final Authentication authentication = SecurityUtils.getAuthentication();
			final MultiRequest multiRequest = new MultiRequest()
					.addRequest("useTraditionalBtn", authentication, "use", traditionalBtn.getData())
					.addRequest("useMultiRequestBtn", authentication, "use", multiRequestBtn.getData())
					.addRequest("useReactiveBtn", authentication, "use", reactiveBtn.getData())
					.addRequest("useReactiveMultiRequestBtn", authentication, "use", reactiveMultiRequestBtn.getData());
			streamManager.setupNewMultiRequest("homeViewButtons", multiRequest);
		}

		traditionalBtn.setEnabled(streamManager.isAccessPermittedForRequestWithId("useTraditionalBtn"));
		multiRequestBtn.setEnabled(streamManager.isAccessPermittedForRequestWithId("useMultiRequestBtn"));
		reactiveBtn.setEnabled(streamManager.isAccessPermittedForRequestWithId("useReactiveBtn"));
		reactiveMultiRequestBtn.setEnabled(streamManager.isAccessPermittedForRequestWithId("useReactiveMultiRequestBtn"));
	}

}
