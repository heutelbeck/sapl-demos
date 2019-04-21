package org.demo.view.reactive.multirequest;

import static io.sapl.api.pdp.multirequest.IdentifiableAction.READ_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableSubject.AUTHENTICATION_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.demo.security.SecurityUtils;
import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.demo.view.reactive.AbstractReactiveView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.multirequest.IdentifiableAction;
import io.sapl.api.pdp.multirequest.IdentifiableResponse;
import io.sapl.api.pdp.multirequest.IdentifiableSubject;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.api.pdp.multirequest.RequestElements;
import io.sapl.spring.PolicyEnforcementPoint;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringView(name = "reactiveMultiRequest")
@SpringComponent("reactiveMultiRequestView")
public class ReactiveMultiRequestView extends AbstractReactiveView {

	private static final String READ_HEART_BEAT_DATA_REQUEST_ID = "readHeartBeatData";
	private static final String READ_BLOOD_PRESSURE_DATA_REQUEST_ID = "readBloodPressureData";

	private final PolicyEnforcementPoint pep;

	private final Map<String, Decision> accessDecisions;

	@Autowired
	public ReactiveMultiRequestView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
									BloodPressureService bloodPressureService) {
		super(heartBeatService, bloodPressureService);
		this.pep = pep;

		accessDecisions = new HashMap<>();
		accessDecisions.put(READ_HEART_BEAT_DATA_REQUEST_ID, Decision.DENY);
		accessDecisions.put(READ_BLOOD_PRESSURE_DATA_REQUEST_ID, Decision.DENY);
	}

	protected Flux<Object[]> getCombinedFlux() {
		final Authentication authentication = SecurityUtils.getAuthentication();

		final MultiRequest multiRequest = new MultiRequest();
		multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
		multiRequest.addAction(new IdentifiableAction(READ_ID, "read"));
		multiRequest.addResource("heartBeatData");
		multiRequest.addResource("bloodPressureData");
		multiRequest.addRequest(READ_HEART_BEAT_DATA_REQUEST_ID,
				new RequestElements(AUTHENTICATION_ID, READ_ID, "heartBeatData"));
		multiRequest.addRequest(READ_BLOOD_PRESSURE_DATA_REQUEST_ID,
				new RequestElements(AUTHENTICATION_ID, READ_ID, "bloodPressureData"));

		final Flux<MultiResponse> accessDecisionFlux = pep.filterEnforce(multiRequest)
				.subscribeOn(Schedulers.newElastic("pdp"));

		return Flux.combineLatest(accessDecisionFlux, getHeartBeatDataFlux(), getDiastolicBloodPressureDataFlux(),
				getSystolicBloodPressureDataFlux(), Function.identity());
	}

	protected void updateUI(Object[] fluxValues) {
		final MultiResponse multiResponse = (MultiResponse) fluxValues[0];
		for (IdentifiableResponse identifiableResponse : multiResponse) {
			accessDecisions.put(identifiableResponse.getRequestId(), identifiableResponse.getResponse().getDecision());
		}

		final Decision heartBeatDecision = accessDecisions.get(READ_HEART_BEAT_DATA_REQUEST_ID);
		final Decision bloodPressureDecision = accessDecisions.get(READ_BLOOD_PRESSURE_DATA_REQUEST_ID);

		final Integer heartBeat = (Integer) fluxValues[1];
		final Integer diastolic = (Integer) fluxValues[2];
		final Integer systolic = (Integer) fluxValues[3];

		updateUI(heartBeatDecision, bloodPressureDecision, heartBeat, diastolic, systolic);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		super.beforeLeave(event);
	}
}
