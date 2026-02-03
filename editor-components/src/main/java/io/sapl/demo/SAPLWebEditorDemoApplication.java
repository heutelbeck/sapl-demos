package io.sapl.demo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Serial;

@ColorScheme(ColorScheme.Value.DARK)
@SpringBootApplication
public class SAPLWebEditorDemoApplication implements AppShellConfigurator {

    @Serial
    private static final long serialVersionUID = -8117421629399543738L;

    public static void main(String[] args) {
        SpringApplication.run(SAPLWebEditorDemoApplication.class, args);
    }

}
