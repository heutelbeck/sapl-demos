package io.sapl.playground;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "saplplayground", variant = Lumo.DARK)
public class SAPLPlaygroundApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(SAPLPlaygroundApplication.class, args);
	}

}