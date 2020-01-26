package org.demo;

import java.util.Collections;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 5937519713416835194L;

	public static final String ROUTE = "login";

	private LoginOverlay login = new LoginOverlay();

	public LoginView() {
		login.setAction("login");
		login.setOpened(true);
		login.setTitle("Printer Access");
		login.setDescription("Login for 3D printing");
		getElement().appendChild(login.getElement());
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) { //

		if (!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList())
				.isEmpty()) {
			login.setError(true); //

		}
	}
}
