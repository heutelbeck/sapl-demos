package org.demo.view.reactive;

import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.vaadin.hezamu.canvas.Canvas;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.api.pdp.Decision;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public abstract class AbstractReactiveView extends VerticalLayout implements View {

    private HeartBeatService heartBeatService;
    private BloodPressureService bloodPressureService;

    private Label heartBeatAccessDenied;
    private Canvas heartBeatCanvas;

    private Label bloodPressureAccessDenied;
    private Canvas bloodPressureCanvas;

    private Thread fluxSubscriptionThread;
    private Disposable subscription;

    protected AbstractReactiveView(HeartBeatService heartBeatService, BloodPressureService bloodPressureService) {
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
        bloodPressureCanvas.setHeight("40px");
        bloodPressureCanvas.setVisible(true);
        bloodPressureCard.addComponents(bloodPressureLabel, bloodPressureAccessDenied, bloodPressureCanvas);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final Flux<Object[]> combinedFlux = getCombinedFlux();

        // subscribe in a separate thread to give the current thread the chance to unlock the vaadin session;
        // otherwise getUI().access(() -> {}) within updateUI() could not acquire the lock necessary to update the UI
        fluxSubscriptionThread = new Thread(() ->
            subscription = combinedFlux.subscribe(
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

    protected Flux<Integer> getHeartBeatDataFlux() {
        return heartBeatService.getHeartBeatData()
                .subscribeOn(Schedulers.newElastic("hb-data"));
    }

    protected Flux<Integer> getDiastolicBloodPressureDataFlux() {
        return bloodPressureService.getDiastolicBloodPressureData()
                .subscribeOn(Schedulers.newElastic("bpd-data"));
    }

    protected Flux<Integer> getSystolicBloodPressureDataFlux() {
        return bloodPressureService.getSystolicBloodPressureData()
                .subscribeOn(Schedulers.newElastic("bps-data"));
    }

    protected abstract Flux<Object[]> getCombinedFlux();

    protected abstract void updateUI(Object[] fluxValues);

    protected void updateUI(Decision heartBeatDecision, Decision bloodPressureDecision,
                            Integer heartBeat, Integer diastolic, Integer systolic) {
        getUI().access(() -> {
            if (heartBeatDecision == Decision.PERMIT) {
                heartBeatAccessDenied.setVisible(false);
                heartBeatCanvas.setVisible(true);
                drawHeartBeat(heartBeat);
            }
            else {
                heartBeatCanvas.setVisible(false);
                heartBeatAccessDenied.setVisible(true);
            }

            if (bloodPressureDecision == Decision.PERMIT) {
                bloodPressureAccessDenied.setVisible(false);
                bloodPressureCanvas.setVisible(true);
                drawBloodPressure(diastolic, systolic);
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
}
