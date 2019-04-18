package org.demo.security;

import static io.sapl.api.pdp.Decision.PERMIT;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;

import io.sapl.spring.PolicyEnforcementPoint;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DemoViewAccessControl implements ViewAccessControl {

	private final PolicyEnforcementPoint pep;

	@Override
	public boolean isAccessGranted(UI ui, String beanName) {
		final Authentication authentication = SecurityUtils.getAuthentication();
		return pep.enforce(authentication, "access", beanName).blockFirst() == PERMIT;
	}
}
