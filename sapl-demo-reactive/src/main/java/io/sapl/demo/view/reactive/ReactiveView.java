package io.sapl.demo.view.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.hezamu.canvas.Canvas;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.api.pdp.Decision;
import io.sapl.demo.service.BloodPressureService;
import io.sapl.demo.service.HeartBeatService;
import io.sapl.pep.SAPLAuthorizer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringComponent("reactiveView")
@SpringView(name = "reactive")
public class ReactiveView extends VerticalLayout implements View {

    private SAPLAuthorizer authorizer;
    private HeartBeatService heartBeatService;
    private BloodPressureService bloodPressureService;

    private Label heartBeatAccessDenied;
    private Canvas heartBeatCanvas;

    private Label bloodPressureAccessDenied;
    private Canvas bloodPressureCanvas;

    private Thread fluxSubscriptionThread;
    private Disposable subscription;

    @Autowired
    public ReactiveView(SAPLAuthorizer authorizer, HeartBeatService heartBeatService, BloodPressureService bloodPressureService) {
        this.authorizer = authorizer;
        this.heartBeatService = heartBeatService;
        this.bloodPressureService = bloodPressureService;

        setSpacing(true);
        setMargin(true);

        final VerticalLayout heatBeatCard = new VerticalLayout();
        heatBeatCard.setSizeFull();
        heatBeatCard.setStyleName(ValoTheme.LAYOUT_CARD);
        addComponent(heatBeatCard);

        final Label heartBeatLabel = new Label("Heart Beat: ");
        heartBeatAccessDenied = new Label("You have no access to heart beat data.");
        heartBeatAccessDenied.setStyleName(ValoTheme.LABEL_FAILURE);
        heartBeatAccessDenied.setVisible(false);
        heartBeatCanvas = new Canvas();
        heartBeatCanvas.setWidth("350px");
        heartBeatCanvas.setHeight("40px");
        heartBeatCanvas.setVisible(true);
        heatBeatCard.addComponents(heartBeatLabel, heartBeatAccessDenied, heartBeatCanvas);

        final VerticalLayout bloodPressureCard = new VerticalLayout();
        bloodPressureCard.setSizeFull();
        bloodPressureCard.setStyleName(ValoTheme.LAYOUT_CARD);
        addComponent(bloodPressureCard);

        final Label bloodPressureLabel = new Label("Blood Pressure: ");
        bloodPressureAccessDenied = new Label("You have no access to blood pressure data.");
        bloodPressureAccessDenied.setStyleName(ValoTheme.LABEL_FAILURE);
        bloodPressureAccessDenied.setVisible(false);
        bloodPressureCanvas = new Canvas();
        bloodPressureCanvas.setWidth("350px");
        bloodPressureCanvas.setHeight("100px");
        bloodPressureCanvas.setVisible(true);
        bloodPressureCard.addComponents(bloodPressureLabel, bloodPressureAccessDenied, bloodPressureCanvas);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Flux<Decision> heartBeatAccessDecisionFlux = authorizer.authorize(authentication, "read", "heartBeatData")
                .subscribeOn(Schedulers.newElastic("hb-pdp"));
        final Flux<Decision> bloodPressureAccessDecisionFlux = authorizer.authorize(authentication, "read", "bloodPressureData")
                .subscribeOn(Schedulers.newElastic("bp-pdp"));

        final Flux<Integer> heartBeatDataFlux = heartBeatService.getHeartBeatData()
                .subscribeOn(Schedulers.newElastic("hb-data"));
        final Flux<Integer> diastolicBloodPressureDataFlux = bloodPressureService.getDiastolicBloodPressureData()
                .subscribeOn(Schedulers.newElastic("bpd-data"));
        final Flux<Integer> systolicBloodPressureDataFlux = bloodPressureService.getSystolicBloodPressureData()
                .subscribeOn(Schedulers.newElastic("bps-data"));

        // subscribe in a separate thread to give the current thread the chance to unlock the vaadin session;
        // otherwise getUI().access(() -> {}) within updateUI() could not acquire the lock necessary to update the UI
        fluxSubscriptionThread = new Thread(() ->
                subscription = Flux.combineLatest(
                        heartBeatAccessDecisionFlux,
                        heartBeatDataFlux,
                        bloodPressureAccessDecisionFlux,
                        diastolicBloodPressureDataFlux,
                        systolicBloodPressureDataFlux,
                        values -> {
                            final FluxValues fluxValues = new FluxValues();
                            fluxValues.heartBeatDecision = (Decision) values[0];
                            fluxValues.heartBeat = (Integer) values[1];
                            fluxValues.bloodPressureDecision = (Decision) values[2];
                            fluxValues.diastolic = (Integer) values[3];
                            fluxValues.systolic = (Integer) values[4];
                            return fluxValues;
                        })
                        .subscribe(
                                this::updateUI,
                                error -> Notification.show(error.getMessage(), Notification.Type.ERROR_MESSAGE)
                        )
        );
        fluxSubscriptionThread.start();
    }

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        subscription.dispose();
        fluxSubscriptionThread.interrupt();
        event.navigate();
    }

    private void updateUI(FluxValues fluxValues) {
        getUI().access(() -> {
            if (fluxValues.heartBeatDecision == Decision.PERMIT) {
                heartBeatAccessDenied.setVisible(false);
                heartBeatCanvas.setVisible(true);
                drawHeartBeat(fluxValues.heartBeat);
            }
            else {
                heartBeatCanvas.setVisible(false);
                heartBeatAccessDenied.setVisible(true);
            }

            if (fluxValues.bloodPressureDecision == Decision.PERMIT) {
                bloodPressureAccessDenied.setVisible(false);
                bloodPressureCanvas.setVisible(true);
                drawBloodPressure(fluxValues.diastolic, fluxValues.systolic);
            }
            else {
                bloodPressureCanvas.setVisible(false);
                bloodPressureAccessDenied.setVisible(true);
            }

        });
    }

    private void drawHeartBeat(int heartBeat) {
        heartBeatCanvas.saveContext();

        heartBeatCanvas.clear();

        heartBeatCanvas.moveTo(0, 0);
        heartBeatCanvas.setFillStyle("red");
        heartBeatCanvas.fillRect(0,  0,  heartBeat,  20);

        heartBeatCanvas.setFont("italic bold 12px sans-serif");
        heartBeatCanvas.setTextBaseline("top");
        heartBeatCanvas.fillText("" + heartBeat + " bpm", heartBeat + 10, 4, 350);

        heartBeatCanvas.restoreContext();
    }

    private void drawBloodPressure(int diastolic, int systolic) {
        bloodPressureCanvas.saveContext();

        bloodPressureCanvas.clear();

        bloodPressureCanvas.setFont("italic bold 12px sans-serif");
        bloodPressureCanvas.setTextBaseline("top");

        bloodPressureCanvas.moveTo(0, 0);

        bloodPressureCanvas.setFillStyle("green");
        bloodPressureCanvas.fillRect(0,  0,  diastolic,  12);
        bloodPressureCanvas.fillText("" + diastolic + " mmHg (diastolic)", diastolic + 10, 0, 350);

        bloodPressureCanvas.setFillStyle("blue");
        bloodPressureCanvas.fillRect(0,  20,  systolic,  12);
        bloodPressureCanvas.fillText("" + systolic + " mmHg (systolic)", systolic + 10, 20, 350);

        bloodPressureCanvas.restoreContext();
    }


    private static class FluxValues {
        Decision heartBeatDecision;
        Integer heartBeat;
        Decision bloodPressureDecision;
        Integer diastolic;
        Integer systolic;
    }
}
