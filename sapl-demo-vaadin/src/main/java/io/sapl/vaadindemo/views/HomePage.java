package io.sapl.vaadindemo.views;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadindemo.shared.Utilities;
import jakarta.annotation.security.PermitAll;

/**
 * This page is used as a landing page for demonstration of routing functionality.
 */
@PermitAll
@PageTitle("Home Page")
@Route(value = "", layout = MainLayout.class)
public class HomePage extends VerticalLayout {

	private static final String INFOTEXT = "This page shows your current user role. Furthermore it is  used as a " +
			"landing page for demonstration of navigation functionalities.";

	public HomePage() {
		var name = SecurityContextHolder.getContext().getAuthentication().getName();
		String greeting = "Welcome '" + name + "'!";
		add(Utilities.getInfoText(INFOTEXT));
		add(Utilities.getDefaultHeader(greeting));
		Utilities.setEmptyLayout(this);
	}

}
