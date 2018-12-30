package io.sapl.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;

import io.sapl.api.SAPLAuthorizer;

@Component
public class SaplViewAccessControl implements ViewAccessControl {

    @Autowired
    private SAPLAuthorizer authorizer;

    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (beanName.equals("homeView")) {
            return authorizer.authorize(authentication, "get", "home");
        } else if (beanName.endsWith("PatientView")) {
            return authorizer.authorize(authentication, "get", "profiles");
        } else if (beanName.startsWith("reactive")) {
            return true;
        } else {
            return SecurityUtils.hasRole("DOCTOR");
        }
    }
}
