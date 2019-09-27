package org.demo.view.reactive;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.demo.model.TimeScheduleData;
import org.demo.security.SecurityUtils;
import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.demo.service.TimeScheduleService;
import org.springframework.security.core.Authentication;
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
import io.sapl.spring.PolicyEnforcementPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Abstract base class of live data views. Concrete subclasses demonstrating the
 * usage of SAPL single requests and SAPL multi-requests directly updating the
 * frontend must implement the method {@link #getCombinedFluxForNonFilteredResources()}.
 */
@Slf4j
public abstract class AbstractReactiveView extends VerticalLayout implements View {

	protected transient PolicyEnforcementPoint pep;

	private ObjectMapper mapper;

	private HeartBeatService heartBeatService;

	private BloodPressureService bloodPressureService;

	private final TimeScheduleService timeScheduleService;

	private Label heartBeatAccessDenied;

	private Canvas heartBeatCanvas;

	private Label bloodPressureAccessDenied;

	private Canvas bloodPressureCanvas;

	private Label timeScheduleDataLabel;

	private Label timeScheduleAccessDenied;

	private Disposable subscription1;

	private Disposable subscription2;

	protected Scheduler nonUIThread;

	protected AbstractReactiveView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, TimeScheduleService timeScheduleService) {
		this.pep = pep;

		this.mapper = new ObjectMapper();
		this.mapper.registerModule(new Jdk8Module());

		this.heartBeatService = heartBeatService;
		this.bloodPressureService = bloodPressureService;
		this.timeScheduleService = timeScheduleService;

		final ExecutorService executorService = Executors.newFixedThreadPool(8);
		this.nonUIThread = Schedulers.fromExecutor(executorService);

		setupUI();
	}

	private void setupUI() {
		setSpacing(true);
		setMargin(true);

		final VerticalLayout heartBeatCard = new VerticalLayout();
		heartBeatCard.setSizeFull();
		heartBeatCard.setStyleName(ValoTheme.LAYOUT_CARD);
		addComponent(heartBeatCard);

		final Label heartBeatLabel = new Label("Heart Beat: ");
		heartBeatAccessDenied = new Label("You have no access to heart beat data.");
		heartBeatAccessDenied.setStyleName(ValoTheme.LABEL_FAILURE);
		heartBeatAccessDenied.setVisible(false);
		heartBeatCanvas = new Canvas();
		heartBeatCanvas.setWidth("350px");
		heartBeatCanvas.setHeight("40px");
		heartBeatCanvas.setVisible(true);
		heartBeatCard.addComponents(heartBeatLabel, heartBeatAccessDenied,
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
		timeScheduleDataLabel = new Label();
		timeScheduleDataLabel.setVisible(false);
		timeScheduleAccessDenied = new Label("You have no access to scheduler data.");
		timeScheduleAccessDenied.setStyleName(ValoTheme.LABEL_FAILURE);
		timeScheduleAccessDenied.setVisible(false);
		schedulerCard.addComponents(schedulerLabel, timeScheduleDataLabel, timeScheduleAccessDenied);
	}

	/**
	 * Subscribes to the streams providing non filtered data (heart beat and blood pressure)
	 * and filtered data (time schedule). Both streams combine data streams returned by data
	 * services and authorization streams returned by the policy decision point.
	 * The subscriptions are stored for later disposal when leaving the view.
	 *
	 * @param event the view-change-event (not used).
	 */
	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		subscription1 = getCombinedFluxForNonFilteredResources().subscribe(
				this::updateUIForNonFilteredResources,
				error -> Notification.show(error.getMessage(), Notification.Type.ERROR_MESSAGE)
		);

		subscription2 = getFilteredResourceFlux().subscribe(
				this::updateUIWithFilteredResource,
				error -> Notification.show(error.getMessage(), Notification.Type.ERROR_MESSAGE)
		);
	}

	/**
	 * Disposes the two subscriptions created on {@link #enter(ViewChangeListener.ViewChangeEvent)}.
	 *
	 * @param event the view-before-leave-event used to navigate to the target view.
	 */
	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		subscription1.dispose();
		subscription2.dispose();
		event.navigate();
	}

	/**
	 * @return a stream providing heart beat data.
	 */
	protected Flux<Integer> getHeartBeatDataFlux() {
		return heartBeatService.getHeartBeatData()
				.distinctUntilChanged()
				.subscribeOn(nonUIThread);
	}

	/**
	 * @return a stream providing diastolic blood pressure data.
	 */
	protected Flux<Integer> getDiastolicBloodPressureDataFlux() {
		return bloodPressureService.getDiastolicBloodPressureData()
				.distinctUntilChanged()
				.subscribeOn(nonUIThread);
	}

	/**
	 * @return a stream providing systolic blood pressure data.
	 */
	protected Flux<Integer> getSystolicBloodPressureDataFlux() {
		return bloodPressureService.getSystolicBloodPressureData()
				.distinctUntilChanged()
				.subscribeOn(nonUIThread);
	}

	/**
	 * @return a stream providing combined values needed for updating the heart beat panel
	 *         and the blood pressure panel. The combined data consists of heart beat and
	 *         blood pressure data and related authorization decisions.
	 */
	protected abstract Flux<NonFilteredResourcesData> getCombinedFluxForNonFilteredResources();

	private void updateUIForNonFilteredResources(NonFilteredResourcesData data) {
		getUI().access(() -> {
			if (data.heartBeatDecision == Decision.PERMIT) {
				heartBeatAccessDenied.setVisible(false);
				heartBeatCanvas.setVisible(true);
				drawHeartBeat(data.heartBeat);
			}
			else {
				heartBeatCanvas.setVisible(false);
				heartBeatAccessDenied.setVisible(true);
			}

			if (data.bloodPressureDecision == Decision.PERMIT) {
				bloodPressureAccessDenied.setVisible(false);
				bloodPressureCanvas.setVisible(true);
				drawBloodPressure(data.diastolic, data.systolic);
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

	private Flux<Response> getFilteredResourceFlux() {
		// Each time the data flux emits a new resource, we have to send an authorization
		// request to the PDP to transform / filter the resource.
		// In this example there is just one resource flux. If there were more than one,
		// we would have to execute the following lines of code for each of them. It would
		// not make much sense to create a multi request with all the resources if only one
		// of them has changed.
		final Authentication authentication = SecurityUtils.getAuthentication();
		return timeScheduleService.getData().switchMap(
				data -> pep.filterEnforce(authentication, "readSchedulerData", data)
						.subscribeOn(nonUIThread));
	}

	private void updateUIWithFilteredResource(Response response) {
		final Decision decision = response.getDecision();
		final TimeScheduleData[] timeScheduleDataHolder = new TimeScheduleData[] { new TimeScheduleData() };
		final Optional<JsonNode> resource = response.getResource();
		if (resource.isPresent()) {
			try {
				timeScheduleDataHolder[0] = mapper.treeToValue(resource.get(), TimeScheduleData.class);
			}
			catch (JsonProcessingException e) {
				LOGGER.error("Error while unmarshalling TimeScheduleData: {}", e.getMessage());
			}
		}
		getUI().access(() -> {
			if (decision == Decision.PERMIT) {
				timeScheduleDataLabel.setValue(timeScheduleDataHolder[0].toString());
				timeScheduleDataLabel.setVisible(true);
				timeScheduleAccessDenied.setVisible(false);
			}
			else {
				timeScheduleDataLabel.setValue("");
				timeScheduleDataLabel.setVisible(false);
				timeScheduleAccessDenied.setVisible(true);
			}
		});
	}

	/**
	 * Data structure holding the results emitted by the flux combining
	 * non filtered resources and their related access decisions.
	 */
	public static class NonFilteredResourcesData {
		public Decision heartBeatDecision;
		public Decision bloodPressureDecision;
		public Integer heartBeat;
		public Integer diastolic;
		public Integer systolic;
	}
}
