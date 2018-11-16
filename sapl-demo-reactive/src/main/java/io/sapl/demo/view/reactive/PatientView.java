package io.sapl.demo.view.reactive;

import java.time.Duration;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import reactor.core.publisher.Flux;

@SpringComponent("reactivePatientView")
@SpringView(name = "reactive")
public class PatientView extends VerticalLayout implements View {

    private Label infoLabel;
    private Label dataLabel;

    public PatientView() {
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
        Flux<Boolean> responses = Flux.just(false, true, false, true, false, true, false, true)
                .delayElements(Duration.ofSeconds(2));

        responses.subscribe(
                this::updateLabel,
                error -> Notification.show(error.getMessage(), Notification.Type.ERROR_MESSAGE)
        );
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
