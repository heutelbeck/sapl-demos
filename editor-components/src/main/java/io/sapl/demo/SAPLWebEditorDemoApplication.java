package io.sapl.demo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import io.sapl.api.SaplVersion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Serial;

@ColorScheme(ColorScheme.Value.LIGHT_DARK)
@SpringBootApplication
public class SAPLWebEditorDemoApplication implements AppShellConfigurator {

    @Serial
    private static final long serialVersionUID = SaplVersion.VERSION_UID;

    public static void main(String[] args) {
        SpringApplication.run(SAPLWebEditorDemoApplication.class, args);
    }

}
