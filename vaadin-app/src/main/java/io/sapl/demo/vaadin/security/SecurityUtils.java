package io.sapl.demo.vaadin.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import lombok.experimental.UtilityClass;

/**
 * Security Utility for Logout
 */
@UtilityClass
public class SecurityUtils {

    private static final String LOGOUT_SUCCESS_URL = "/";

    /**
     * Invalidate Session and Logout
     */
    public static void logout() {
        var page = UI.getCurrent().getPage();
        page.setLocation(LOGOUT_SUCCESS_URL);
        VaadinSession.getCurrent().getSession().invalidate();
    }

}