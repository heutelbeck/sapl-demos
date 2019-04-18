package org.demo.view;

import static io.sapl.api.pdp.multirequest.IdentifiableSubject.AUTHENTICATION_ID;

import org.demo.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.api.pdp.multirequest.IdentifiableSubject;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.api.pdp.multirequest.RequestElements;
import io.sapl.spring.PolicyEnforcementPoint;

@SpringView(name = "") // Root view
public class HomeView extends VerticalLayout implements View {

    private PolicyEnforcementPoint pep;

    @Autowired
    public HomeView(PolicyEnforcementPoint pep) {
        this.pep = pep;

        setMargin(true);
        setSpacing(true);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final String username = SecurityUtils.getUsername();
        final Label label = new Label("Welcome " + username + "!");
        label.setStyleName(ValoTheme.LABEL_LARGE);
        addComponent(label);

        final Button traditionalBtn = new Button("Show Patient List (blocking, single requests)",
                click -> getUI().getNavigator().navigateTo("traditional")
        );
        traditionalBtn.setData("ui:view:home:showPatientListTraditionalButton");

        final Button multiRequestBtn = new Button("Show Patient List (blocking, multi-request)",
                click -> getUI().getNavigator().navigateTo("multiRequest")
        );
        multiRequestBtn.setData("ui:view:home:showPatientListMultiRequestButton");

        final Button reactiveBtn = new Button("Show Reactive View",
                click -> getUI().getNavigator().navigateTo("reactive")
        );
        reactiveBtn.setData("ui:view:home:showReactiveViewButton");

        final Button reactiveMultiRequestBtn = new Button("Show Reactive View (multi-request)",
                click -> getUI().getNavigator().navigateTo("reactiveMultiRequest")
        );
        reactiveMultiRequestBtn.setData("ui:view:home:showReactiveViewMultiRequestButton");

        final MultiRequest multiRequest = new MultiRequest();
        multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, SecurityUtils.getAuthentication()));
        multiRequest.addAction("use");
        multiRequest.addResource((String) traditionalBtn.getData());
        multiRequest.addResource((String) multiRequestBtn.getData());
        multiRequest.addResource((String) reactiveBtn.getData());
        multiRequest.addResource((String) reactiveMultiRequestBtn.getData());
        multiRequest.addRequest("useTraditionalBtn", new RequestElements(AUTHENTICATION_ID, "use", (String) traditionalBtn.getData()));
        multiRequest.addRequest("useMultiRequestBtn", new RequestElements(AUTHENTICATION_ID, "use", (String) multiRequestBtn.getData()));
        multiRequest.addRequest("useReactiveBtn", new RequestElements(AUTHENTICATION_ID, "use", (String) reactiveBtn.getData()));
        multiRequest.addRequest("useReactiveMultiRequestBtn", new RequestElements(AUTHENTICATION_ID, "use", (String) reactiveMultiRequestBtn.getData()));
        final MultiResponse multiResponse = pep.filterEnforce(multiRequest).blockFirst();

        traditionalBtn.setEnabled(multiResponse.isAccessPermittedForRequestWithId("useTraditionalBtn"));
        multiRequestBtn.setEnabled(multiResponse.isAccessPermittedForRequestWithId("useTraditionalBtn"));
        reactiveBtn.setEnabled(multiResponse.isAccessPermittedForRequestWithId("useTraditionalBtn"));
        reactiveMultiRequestBtn.setEnabled(multiResponse.isAccessPermittedForRequestWithId("useTraditionalBtn"));

        addComponents(traditionalBtn, multiRequestBtn, reactiveBtn, reactiveMultiRequestBtn);
    }

}
