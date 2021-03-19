package org.demo.view;

import org.demo.security.SecurityUtils;
import org.demo.view.traditional.multisubscription.MultiSubscriptionStreamManager;
import org.springframework.security.core.Authentication;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.api.pdp.MultiAuthorizationSubscription;

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

		final Button traditionalBtn = new Button("Show Patient View (session based, single subscriptions)",
				click -> getUI().getNavigator().navigateTo("traditional"));
		traditionalBtn.setData("ui:view:home:showPatientListTraditionalButton");
		traditionalBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(traditionalBtn, 0, 0);

		final Button multiSubscriptionBtn = new Button("Show Patient View (session based, multi-subscriptions)",
				click -> getUI().getNavigator().navigateTo("multiSubscription"));
		multiSubscriptionBtn.setData("ui:view:home:showPatientListMultiSubscriptionButton");
		multiSubscriptionBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(multiSubscriptionBtn, 0, 1);

		final Button reactiveBtn = new Button("Show Live-Data View (reactive frontend, single subscriptions)",
				click -> getUI().getNavigator().navigateTo("reactive"));
		reactiveBtn.setData("ui:view:home:showReactiveViewButton");
		reactiveBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(reactiveBtn, 0, 3);

		final Button reactiveMultiSubscriptionBtn = new Button(
				"Show Live-Data View (reactive frontend, multi-subscriptions)",
				click -> getUI().getNavigator().navigateTo("reactiveMultiSubscription"));
		reactiveMultiSubscriptionBtn.setData("ui:view:home:showReactiveViewMultiSubscriptionButton");
		reactiveMultiSubscriptionBtn.setWidth(BUTTON_WIDTH);
		grid.addComponent(reactiveMultiSubscriptionBtn, 0, 4);

		final MultiSubscriptionStreamManager streamManager = getSession()
				.getAttribute(MultiSubscriptionStreamManager.class);
		if (!streamManager.hasSubscriptionFor("homeViewButtons")) {
			final Authentication authentication = SecurityUtils.getAuthentication();
			final MultiAuthorizationSubscription multiSubscription = new MultiAuthorizationSubscription()
					.addAuthorizationSubscription("useTraditionalBtn", authentication, "use", traditionalBtn.getData())
					.addAuthorizationSubscription("useMultiSubscriptionBtn", authentication, "use",
							multiSubscriptionBtn.getData())
					.addAuthorizationSubscription("useReactiveBtn", authentication, "use", reactiveBtn.getData())
					.addAuthorizationSubscription("useReactiveMultiSubscriptionBtn", authentication, "use",
							reactiveMultiSubscriptionBtn.getData());
			streamManager.setupNewMultiSubscription("homeViewButtons", multiSubscription);
		}

		traditionalBtn
				.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("useTraditionalBtn"));
		multiSubscriptionBtn.setEnabled(
				streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("useMultiSubscriptionBtn"));
		reactiveBtn.setEnabled(streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("useReactiveBtn"));
		reactiveMultiSubscriptionBtn.setEnabled(
				streamManager.isAccessPermittedForAuthorizationSubscriptionWithId("useReactiveMultiSubscriptionBtn"));
	}

}
