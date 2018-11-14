package io.sapl.demo.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import io.sapl.demo.service.BackendService;
import io.sapl.spring.SAPLAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringView(name = "") // Root view
public class HomeView extends VerticalLayout implements View {

    @Autowired
    private SAPLAuthorizer authorizer;

    @Autowired
    private BackendService backendService;

    public HomeView() {
        setMargin(true);
        setSpacing(true);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String username = authentication.getName();
        final Label label = new Label("Hello " + username);
        label.setStyleName(ValoTheme.LABEL_H1);
        addComponent(label);

        final Button btn = new Button("Show Patient List",
                click -> getUI().getNavigator().navigateTo("patient")
        );
        btn.setEnabled(authorizer.wouldAuthorize(authentication, "get", "profiles"));

        addComponent(btn);

        backendService.demonstrateUsageOfPdpAuthorizeAnnotation();
    }
}
