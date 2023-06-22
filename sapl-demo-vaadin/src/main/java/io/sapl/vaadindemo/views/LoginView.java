package io.sapl.vaadindemo.views;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        login.setAction("login");
        add(new H1("SAPL Vaadin Demo"), login);
        this.createUserList();
    }

    private void createUserList() {
        add(new H3("User list"));
        Div contentContainer = new Div();
        Map<String, Component> tabComponentMap = createUserTabs();
        var tabs = new Tabs();
        tabComponentMap.forEach((title, component) -> tabs.add(new Tab(title)));
        tabs.addSelectedChangeListener(event -> {
                    contentContainer.removeAll();
                    contentContainer.add(tabComponentMap.get(event.getSelectedTab().getLabel()));
                }
        );
        // init
        contentContainer.add(tabComponentMap.get(tabs.getSelectedTab().getLabel()));
        add(tabs, contentContainer);
    }

    private Map<String, Component> createUserTabs() {
        Map<String, Component> tabComponentMap = new LinkedHashMap<>();
        tabComponentMap.put("admin", new Html("<div>password: admin<br/>Can access all pages and perform all actions.</div>"));
        tabComponentMap.put("user", new Html("<div>password: user<br/>Can´t navigate to'Admin Page'.</div>"));
        return tabComponentMap;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}
