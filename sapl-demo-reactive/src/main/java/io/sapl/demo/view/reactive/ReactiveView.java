package io.sapl.demo.view.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.api.SAPLAuthorizer;
import io.sapl.api.pdp.Decision;
import reactor.core.publisher.Flux;

@SpringComponent("reactiveView")
@SpringView(name = "reactive")
public class ReactiveView extends VerticalLayout implements View {

    @Autowired
    private SAPLAuthorizer authorizer;

    private Label infoLabel;
    private Label dataLabel;

    public ReactiveView() {
        setSpacing(true);
        setMargin(true);

        infoLabel = new Label();
        infoLabel.setValue("You are authorized to see highly sensitive data.");
        infoLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
        addComponent(infoLabel);

        final VerticalLayout card = new VerticalLayout();
        card.setSizeFull();
        card.setStyleName(ValoTheme.LAYOUT_CARD);
        addComponent(card);

        dataLabel = new Label("Highly sensitive data!!!");
        dataLabel.addStyleNames(ValoTheme.LABEL_COLORED, ValoTheme.LABEL_BOLD);
        card.addComponent(dataLabel);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Flux<Decision> decisionFlux = authorizer.reactiveAuthorize(authentication, "readSensitiveData", "sensitiveData");

        // subscribe in a separate thread to give the current thread the chance to unlock the vaadin session;
        // otherwise getUI().access(() -> {}) within updateLabel() could not acquire the lock necessary to update the UI
        final Thread fluxSubscriptionThread = new Thread(() ->
                decisionFlux
                    .map(decision -> decision == Decision.PERMIT)
                    .subscribe(
                        this::updateLabel,
                        error -> Notification.show(error.getMessage(), Notification.Type.ERROR_MESSAGE)
                    )
        );
        fluxSubscriptionThread.start();
    }

    private void updateLabel(boolean response) {
        getUI().access(() -> {
                if (response) {
                    infoLabel.setValue("You are authorized to see highly sensitive data.");
                    infoLabel.setStyleName(ValoTheme.LABEL_SUCCESS);

                    dataLabel.setVisible(true);
                } else {
                    infoLabel.setValue("You are not authorized to see highly sensitive data.");
                    infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);

                    dataLabel.setVisible(false);
                }
        });
    }
}
