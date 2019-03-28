package org.demo.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.vaadin.ui.Notification;

import io.sapl.spring.method.pre.PreEnforce;

@Service
public class BackendService implements Serializable {

	@PreEnforce(action = "'get'", resource = "'profiles'")
	public void demonstrateUsageOfPdpAuthorizeAnnotation() {
		Notification.show("You are authorized to load the list of patients.", Notification.Type.HUMANIZED_MESSAGE);
	}
}
