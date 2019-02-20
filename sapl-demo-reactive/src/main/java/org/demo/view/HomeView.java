package org.demo.view;

import org.demo.security.SecurityUtils;
import org.demo.service.BackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.spring.PolicyEnforcementPoint;

@SpringView(name = "") // Root view
public class HomeView extends VerticalLayout implements View {

    private PolicyEnforcementPoint pep;
    private BackendService backendService;

    @Autowired
    public HomeView(PolicyEnforcementPoint pep, BackendService backendService) {
        this.pep = pep;
        this.backendService = backendService;

        setMargin(true);
        setSpacing(true);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final String username = SecurityUtils.getUsername();
        final Label label = new Label("Welcome " + username);
        label.setStyleName(ValoTheme.LABEL_LARGE);
        addComponent(label);

        final Button traditionalBtn = new Button("Show Patient List (Traditional)",
                click -> getUI().getNavigator().navigateTo("traditional")
        );

        final Button multiRequestBtn = new Button("Show Patient List (Multi-Request)",
                click -> getUI().getNavigator().navigateTo("multiRequest")
        );

        final Button reactiveBtn = new Button("Show Reactive View",
                click -> getUI().getNavigator().navigateTo("reactive")
        );

        final Button reactiveMultiRequestBtn = new Button("Show Reactive Multi-Request View",
                click -> getUI().getNavigator().navigateTo("reactiveMultiRequest")
        );

        final boolean canLoadProfiles = canLoadProfiles();
        traditionalBtn.setEnabled(canLoadProfiles);
        multiRequestBtn.setEnabled(canLoadProfiles);
        reactiveBtn.setEnabled(canLoadProfiles);
        reactiveMultiRequestBtn.setEnabled(canLoadProfiles);

        addComponents(traditionalBtn, multiRequestBtn, reactiveBtn, reactiveMultiRequestBtn);

        backendService.demonstrateUsageOfPdpAuthorizeAnnotation();
    }

    private boolean canLoadProfiles() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return pep.enforce(authentication, "get", "profiles");
    }
}
