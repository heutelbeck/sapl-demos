package org.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;

import io.sapl.spring.PolicyEnforcementPoint;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaplViewAccessControl implements ViewAccessControl {

	private final PolicyEnforcementPoint pep;

	@Override
	public boolean isAccessGranted(UI ui, String beanName) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (beanName.equals("homeView")) {
			return pep.enforce(authentication, "get", "home");
		} else if (beanName.endsWith("PatientView")) {
			return pep.enforce(authentication, "get", "profiles");
		} else if (beanName.startsWith("reactive")) {
			return true;
		} else {
			return SecurityUtils.hasRole("DOCTOR");
		}
	}
}
