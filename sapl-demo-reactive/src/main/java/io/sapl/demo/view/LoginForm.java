package io.sapl.demo.view;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

class LoginForm extends VerticalLayout {

    LoginForm(LoginCallback callback) {
        setMargin(true);
        setSpacing(true);

        TextField username = new TextField("Username");
        addComponent(username);

        PasswordField password = new PasswordField("Password");
        addComponent(password);

        Button login = new Button("Login", evt -> {
            String pword = password.getValue();
            password.setValue("");
            if (!callback.login(username.getValue(), pword)) {
                Notification.show("Login failed");
                username.focus();
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        addComponent(login);
    }

    @FunctionalInterface
    public interface LoginCallback {
        boolean login(String username, String password);
    }
}
