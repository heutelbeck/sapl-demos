package org.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.ui.Notification;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SecurityUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAuthenticated() {
        final Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public static String getUsername() {
        final Authentication authentication = getAuthentication();
        return authentication != null ? authentication.getName() : "";
    }

    public static boolean hasRole(String role) {
        final Authentication authentication = getAuthentication();
        return authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    public static void notifyNotAuthorized() {
        Notification.show("You are not authorized to perform the requested operation.", Notification.Type.WARNING_MESSAGE);
    }
}
