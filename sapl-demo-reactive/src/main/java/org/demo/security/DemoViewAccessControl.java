package org.demo.security;

import org.demo.view.traditional.singlerequest.SingleRequestStreamManager;
import org.springframework.stereotype.Component;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;

@Component
public class DemoViewAccessControl implements ViewAccessControl {

	@Override
	public boolean isAccessGranted(UI ui, String beanName) {
		final SingleRequestStreamManager streamManager = ui.getSession().getAttribute(SingleRequestStreamManager.class);
		return streamManager.isAccessPermitted("access", beanName);
	}

}
