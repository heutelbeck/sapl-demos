package io.sapl.vaadindemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@Push
@EnableAsync
@SpringBootApplication
@Theme(value = "collaborationengine")
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
// TODO: bean initialization in framework
@ComponentScan({ "io.sapl.vaadindemo", "io.sapl.vaadin" })
public class VaadinDemoApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	private static final long serialVersionUID = 5743256440001025762L;

	public static void main(String[] args) {
		SpringApplication.run(VaadinDemoApplication.class, args);
	}
}
