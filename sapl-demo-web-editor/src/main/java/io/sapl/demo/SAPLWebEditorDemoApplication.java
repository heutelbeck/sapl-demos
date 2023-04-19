package io.sapl.demo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "sapldemowebeditor", variant = Lumo.DARK)
public class SAPLWebEditorDemoApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(SAPLWebEditorDemoApplication.class, args);
    }

}
