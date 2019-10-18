package org.demo;

import org.demo.security.SecurityUtils;
import org.demo.view.AccessDeniedView;
import org.demo.view.ErrorView;
import org.demo.view.traditional.multisubscription.MultiSubscriptionStreamManager;
import org.demo.view.traditional.singlesubscription.SingleSubscriptionStreamManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.spring.PolicyEnforcementPoint;

@SpringUI
@Theme(ValoTheme.THEME_NAME)
public class DemoUI extends UI {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private SpringNavigator navigator;

	@Autowired
	private SpringViewProvider viewProvider;

	@Autowired
	private ErrorView errorView;

	@Autowired
	private PolicyEnforcementPoint pep;

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Vaadin Spring Security Demo");
		if (!SecurityUtils.isAuthenticated()) {
			showLoginForm();
		}
		else {
			showMainContent();
		}
	}

	private void showLoginForm() {
		setContent(new LoginForm(this::login));
	}

	private void showMainContent() {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();

		final HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		toolbar.setWidth("100%");
		layout.addComponent(toolbar);

		final HorizontalLayout right = new HorizontalLayout();
		right.setSpacing(true);
		right.setSizeUndefined();

		final Button homeBtn = new Button("Home", event -> navigator.navigateTo(""));
		final Label homeLbl = new Label("Reactive SAPL Demo Application");
		homeLbl.setStyleName(ValoTheme.LABEL_HUGE);
		final Label username = new Label("Username: " + SecurityUtils.getUsername());
		final Button logoutBtn = new Button("Logout", event -> logout());
		toolbar.addComponents(homeBtn, homeLbl, right);
		toolbar.setComponentAlignment(homeBtn, Alignment.MIDDLE_LEFT);
		toolbar.setComponentAlignment(right, Alignment.MIDDLE_RIGHT);
		right.addComponents(username, logoutBtn);
		right.setComponentAlignment(username, Alignment.MIDDLE_LEFT);
		right.setComponentAlignment(logoutBtn, Alignment.MIDDLE_RIGHT);

		final Panel viewContainer = new Panel();
		viewContainer.setSizeFull();
		layout.addComponent(viewContainer);
		layout.setExpandRatio(viewContainer, 1.0f);

		setContent(layout);

		setErrorHandler(this::handleError);

		viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
		navigator.init(this, viewContainer);
		navigator.setErrorView(errorView);

		// don't show the home-button on the home-view
		navigator.addViewChangeListener((ViewChangeListener) event -> {
			homeBtn.setVisible(!"".equals(event.getViewName()));
			homeLbl.setVisible("".equals(event.getViewName()));
			return true;
		});

		navigator.navigateTo("");
	}

	private void login(String username, String password) {
		final Authentication token = new UsernamePasswordAuthenticationToken(username, password);
		final Authentication authentication = authenticationManager.authenticate(token);
		VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		getSession().setAttribute(SingleSubscriptionStreamManager.class, new SingleSubscriptionStreamManager(pep));
		getSession().setAttribute(MultiSubscriptionStreamManager.class, new MultiSubscriptionStreamManager(pep));

		getPushConfiguration().setTransport(Transport.WEBSOCKET);
		getPushConfiguration().setPushMode(PushMode.AUTOMATIC);

		showMainContent();
	}

	private void logout() {
		// navigate to the home view to make sure beforeLeave() is called on the reactive views
		getNavigator().navigateTo("");
		getSession().getAttribute(SingleSubscriptionStreamManager.class).dispose();
		getSession().getAttribute(MultiSubscriptionStreamManager.class).dispose();
		getSession().close();
		SecurityContextHolder.clearContext();
	}

	private void handleError(com.vaadin.server.ErrorEvent event) {
		Throwable t = DefaultErrorHandler.findRelevantThrowable(event.getThrowable());
		if (t instanceof AccessDeniedException) {
			SecurityUtils.notifyNotAuthorized();
		}
		else {
			DefaultErrorHandler.doDefault(event);
		}
	}

}
