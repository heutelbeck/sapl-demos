package org.demo;

import org.springframework.security.core.AuthenticationException;

import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

class LoginForm extends VerticalLayout {

	LoginForm(LoginCallback callback) {
		setMargin(true);
		setSpacing(true);

		final HorizontalLayout leftRight = new HorizontalLayout();

		final VerticalLayout left = createLeftPart(callback);
		final VerticalLayout right = createRightPart();
		leftRight.addComponents(left, right);

		addComponentsAndExpand(leftRight);
	}

	private VerticalLayout createLeftPart(LoginCallback callback) {
		final VerticalLayout left = new VerticalLayout();

		final TextField username = new TextField("Username");
		final PasswordField password = new PasswordField("Password");

		final Button login = new Button("Login", evt -> {
			String pwd = password.getValue();
			password.setValue("");
			try {
				callback.login(username.getValue(), pwd);
			}
			catch (AuthenticationException e) {
				Notification.show("Login failed");
				username.focus();
			}
		});
		login.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		left.addComponents(username, password, login);
		return left;
	}

	private VerticalLayout createRightPart() {
		final VerticalLayout right = new VerticalLayout();
		right.setWidth("100%");
		right.setHeight("100%");

		final Label text = new Label();
		text.setWidth("650px");
		text.setHeightUndefined();
		text.setContentMode(ContentMode.HTML);
		// @formatter:off
		text.setValue(
				"<p>This demo is based on a fictional healthcare scenario, where doctors, nurses and family members access health records of patients.</p>"
			  + "<p>You can access the system as one of the following users (the password is always 'password'):"
			     + "<ul>"
			        + "<li><b>Dominic</b> is a visitor and is related to the patient Lenny.</li>"
			        + "<li><b>Peter</b> is a doctor.</li>"
			        + "<li><b>Alina</b> is a doctor and is related to the patient Karl. She is also the attending doctor for the patient Karl.</li>"
			        + "<li><b>Julia</b> is a doctor and is related to the patient Karl. She is also the attending doctor for the patient Lenny.</li>"
			        + "<li><b>Brigitte</b> is a nurse.</li>"
			        + "<li><b>Janosch</b> is a nurse and is related to the patient Karl.</li>"
			        + "<li><b>Janina</b> is a nurse. And she is the attending nurse of the patient Karl.</li>"
			        + "<li><b>Thomas</b> is a nurse. And he is the attending nurse of the patient Lenny.</li>"
			        + "<li><b>Horst</b> is a system administrator.</li>"
			     + "</ul>"
			  + "</p>");
		// @formatter:on

		right.addComponentsAndExpand(text);
		return right;
	}

	@FunctionalInterface
	public interface LoginCallback {

		void login(String username, String password);

	}

}
