package org.demo.view.reactive;

import java.util.Optional;

import org.demo.model.SchedulerData;
import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.demo.service.ScheduleService;
import org.vaadin.hezamu.canvas.Canvas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Response;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public abstract class AbstractReactiveView extends VerticalLayout implements View {

	private ObjectMapper mapper;

	private HeartBeatService heartBeatService;

	private BloodPressureService bloodPressureService;

	private final ScheduleService scheduleService;

	private Label heartBeatAccessDenied;

	private Canvas heartBeatCanvas;

	private Label bloodPressureAccessDenied;

	private Canvas bloodPressureCanvas;

	private Label schedulerDataLabel;

	private Label schedulerAccessDenied;

	private Thread fluxSubscriptionThread1;

	private Disposable subscription1;

	private Thread fluxSubscriptionThread2;

	private Disposable subscription2;

	protected AbstractReactiveView(HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, ScheduleService scheduleService) {
		this.mapper = new ObjectMapper();
		this.mapper.registerModule(new Jdk8Module());

		this.heartBeatService = heartBeatService;
		this.bloodPressureService = bloodPressureService;
		this.scheduleService = scheduleService;

		setupUI();
	}

	private void setupUI() {
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
		heatBeatCard.addComponents(heartBeatLabel, heartBeatAccessDenied,
				heartBeatCanvas);

		final VerticalLayout bloodPressureCard = new VerticalLayout();
		bloodPressureCard.setSizeFull();
		bloodPressureCard.setStyleName(ValoTheme.LAYOUT_CARD);
		addComponent(bloodPressureCard);

		final Label bloodPressureLabel = new Label("Blood Pressure: ");
		bloodPressureAccessDenied = new Label(
				"You have no access to blood pressure data.");
		bloodPressureAccessDenied.setStyleName(ValoTheme.LABEL_FAILURE);
		bloodPressureAccessDenied.setVisible(false);
		bloodPressureCanvas = new Canvas();
		bloodPressureCanvas.setWidth("350px");
		bloodPressureCanvas.setHeight("40px");
		bloodPressureCanvas.setVisible(true);
		bloodPressureCard.addComponents(bloodPressureLabel, bloodPressureAccessDenied,
				bloodPressureCanvas);

		final VerticalLayout schedulerCard = new VerticalLayout();
		schedulerCard.setSizeFull();
		schedulerCard.setStyleName(ValoTheme.LAYOUT_CARD);
		addComponent(schedulerCard);

		final Label schedulerLabel = new Label("Schedule-Ticker:");
		schedulerDataLabel = new Label();
		schedulerDataLabel.setVisible(false);
		schedulerAccessDenied = new Label("You have no access to scheduler data.");
		schedulerAccessDenied.setStyleName(ValoTheme.LABEL_FAILURE);
		schedulerAccessDenied.setVisible(false);
		schedulerCard.addComponents(schedulerLabel, schedulerDataLabel, schedulerAccessDenied);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final Flux<Object[]> combinedFlux = getCombinedFluxForNonFilteredResources();

		// subscribe in a separate thread to give the current thread the chance to unlock
		// the vaadin session;
		// otherwise getUI().access(() -> {}) could not acquire the lock necessary to update the UI
		fluxSubscriptionThread1 = new Thread(() -> subscription1 = combinedFlux
				.subscribe(this::updateUIForNonFilteredResources, error -> Notification.show(error.getMessage(),
						Notification.Type.ERROR_MESSAGE)));
		fluxSubscriptionThread1.start();

		final Flux<Response> responseFlux = getFilteredResourceFlux();
		fluxSubscriptionThread2 = new Thread(() -> subscription2 = responseFlux
				.subscribe(
						this::updateUIWithFilteredResource, error -> Notification.show(error.getMessage(),
								Notification.Type.ERROR_MESSAGE)));
		fluxSubscriptionThread2.start();
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		subscription1.dispose();
		fluxSubscriptionThread1.interrupt();
		subscription2.dispose();
		fluxSubscriptionThread2.interrupt();
		event.navigate();
	}

	protected Flux<Integer> getHeartBeatDataFlux() {
		return heartBeatService.getHeartBeatData()
				.distinctUntilChanged()
				.subscribeOn(Schedulers.newElastic("hb-data"));
	}

	protected Flux<Integer> getDiastolicBloodPressureDataFlux() {
		return bloodPressureService.getDiastolicBloodPressureData()
				.distinctUntilChanged()
				.subscribeOn(Schedulers.newElastic("bpd-data"));
	}

	protected Flux<Integer> getSystolicBloodPressureDataFlux() {
		return bloodPressureService.getSystolicBloodPressureData()
				.distinctUntilChanged()
				.subscribeOn(Schedulers.newElastic("bps-data"));
	}

	protected Flux<SchedulerData> getScheduleDataFlux() {
		return scheduleService.getData()
				.subscribeOn(Schedulers.newElastic("sc-data"));
	}

	protected abstract Flux<Object[]> getCombinedFluxForNonFilteredResources();

	protected abstract void updateUIForNonFilteredResources(Object[] fluxValues);

	protected void updateUIForNonFilteredResources(Decision heartBeatDecision, Decision bloodPressureDecision,
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
		heartBeatCanvas.fillRect(0, 0, heartBeat, 20);

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
		bloodPressureCanvas.fillRect(0, 0, diastolic, 12);
		bloodPressureCanvas.fillText("" + diastolic + " mmHg (diastolic)", diastolic + 10,
				0, 350);

		bloodPressureCanvas.setFillStyle("blue");
		bloodPressureCanvas.fillRect(0, 20, systolic, 12);
		bloodPressureCanvas.fillText("" + systolic + " mmHg (systolic)", systolic + 10,
				20, 350);

		bloodPressureCanvas.restoreContext();
	}

	protected abstract Flux<Response> getFilteredResourceFlux();

	private void updateUIWithFilteredResource(Response response) {
		final Decision decision = response.getDecision();
		final SchedulerData[] schedulerDataHolder = new SchedulerData[1];
		final Optional<JsonNode> resource = response.getResource();
		if (resource.isPresent()) {
			try {
				schedulerDataHolder[0] = mapper.treeToValue(resource.get(), SchedulerData.class);
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
				schedulerDataHolder[0] = new SchedulerData("", "?", "?");
			}
		}
		getUI().access(() -> {
			if (decision == Decision.PERMIT) {
				schedulerDataLabel.setValue(schedulerDataHolder[0].toString());
				schedulerDataLabel.setVisible(true);
				schedulerAccessDenied.setVisible(false);
			}
			else {
				schedulerDataLabel.setValue("");
				schedulerDataLabel.setVisible(false);
				schedulerAccessDenied.setVisible(true);
			}
		});
	}

}
