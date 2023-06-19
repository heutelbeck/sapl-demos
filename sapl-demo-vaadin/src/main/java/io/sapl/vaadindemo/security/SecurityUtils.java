package io.sapl.vaadindemo.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtils {

    private static final String LOGOUT_SUCCESS_URL = "/";

    public static void logout() {
        var page = UI.getCurrent().getPage();
        page.setLocation(LOGOUT_SUCCESS_URL);
        VaadinSession.getCurrent().getSession().invalidate();
    }

}