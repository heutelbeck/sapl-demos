package org.demo;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.vaadin.flow.component.notification.Notification;

@Service
public class PrintService implements Serializable {

	private static final long serialVersionUID = -268367144866825840L;

	public void print(String template) {
		if (template == null || template.isEmpty()) {
			Notification.show("Please select a template");
		}
		else {
			try {
				Thread.sleep(5000L);
			}
			catch (InterruptedException e) {
				Notification.show("Error while printing");
				return;
			}
			Notification.show("Print job successful");
		}
	}

}
