package io.sapl.vaadindemo.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadin.PepBuilderService;
import io.sapl.vaadindemo.shared.Utilities;
import jakarta.annotation.security.PermitAll;

/**
 * The admin page demonstrates the rerouting for all users except admins.
 * Furthermore, it demonstrates notifications.
 */
@PermitAll
@PageTitle("Admin Page")
@Route(value = "admin-page", layout = MainLayout.class)
public class AdminPage extends VerticalLayout implements BeforeEnterObserver {

	private static final String       INFOTEXT         = "The \"Admin Page\" and the notification is only shown if you are an admin. "
			+
			"Otherwise you will be navigated to the home page. The routing protection of this site is " +
			"constructed via the building pattern beginning with the PepBuilderService.";
	private final BeforeEnterListener beforeEnterListener;

	public AdminPage(PepBuilderService builder) {
		this.beforeEnterListener = builder.getLifecycleBeforeEnterPepBuilder()
				.onDenyRerouteTo("/")
				.build();

		add(Utilities.getInfoText(INFOTEXT));
		add(Utilities.getDefaultHeader("Admin Page"));
		Utilities.setEmptyLayout(this);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		beforeEnterListener.beforeEnter(beforeEnterEvent);
	}
}
