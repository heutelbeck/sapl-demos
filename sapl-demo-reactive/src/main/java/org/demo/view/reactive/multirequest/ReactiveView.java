package org.demo.view.reactive.multirequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.demo.model.TimeScheduleData;
import org.demo.security.SecurityUtils;
import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.demo.service.TimeScheduleService;
import org.demo.view.reactive.AbstractReactiveView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Response;
import io.sapl.api.pdp.multirequest.IdentifiableResponse;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.spring.PolicyEnforcementPoint;
import reactor.core.publisher.Flux;

@SpringView(name = "reactiveMultiRequest")
@SpringComponent("reactiveMultiRequestView")
public class ReactiveView extends AbstractReactiveView {

	private static final long serialVersionUID = 1L;

	private static final String READ_HEART_BEAT_DATA_REQUEST_ID = "readHeartBeatData";

	private static final String READ_BLOOD_PRESSURE_DATA_REQUEST_ID = "readBloodPressureData";

	private transient PolicyEnforcementPoint pep;

	private final Map<String, Decision> accessDecisions;

	@Autowired
	public ReactiveView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, TimeScheduleService timeScheduleService) {
		super(heartBeatService, bloodPressureService, timeScheduleService);
		this.pep = pep;

		accessDecisions = new HashMap<>();
		accessDecisions.put(READ_HEART_BEAT_DATA_REQUEST_ID, Decision.DENY);
		accessDecisions.put(READ_BLOOD_PRESSURE_DATA_REQUEST_ID, Decision.DENY);
	}

	protected Flux<Object[]> getCombinedFluxForNonFilteredResources() {
		final Authentication authentication = SecurityUtils.getAuthentication();

		final MultiRequest multiRequest = new MultiRequest()
				.addRequest(READ_HEART_BEAT_DATA_REQUEST_ID, authentication, "read", "heartBeatData")
				.addRequest(READ_BLOOD_PRESSURE_DATA_REQUEST_ID, authentication, "read", "bloodPressureData");

		final Flux<MultiResponse> accessDecisionFlux = pep.filterEnforceAll(multiRequest)
				.subscribeOn(nonUIThread);

		return Flux.combineLatest(accessDecisionFlux, getHeartBeatDataFlux(),
				getDiastolicBloodPressureDataFlux(), getSystolicBloodPressureDataFlux(),
				Function.identity());
	}

	protected void updateUIForNonFilteredResources(Object[] fluxValues) {
		final MultiResponse multiResponse = (MultiResponse) fluxValues[0];
		for (IdentifiableResponse identifiableResponse : multiResponse) {
			accessDecisions.put(identifiableResponse.getRequestId(),
					identifiableResponse.getResponse().getDecision());
		}

		final Decision heartBeatDecision = accessDecisions
				.get(READ_HEART_BEAT_DATA_REQUEST_ID);
		final Decision bloodPressureDecision = accessDecisions
				.get(READ_BLOOD_PRESSURE_DATA_REQUEST_ID);

		final Integer heartBeat = (Integer) fluxValues[1];
		final Integer diastolic = (Integer) fluxValues[2];
		final Integer systolic = (Integer) fluxValues[3];

		updateUIForNonFilteredResources(heartBeatDecision, bloodPressureDecision,
				heartBeat, diastolic, systolic);
	}

	@Override
	protected Flux<Response> getFilteredResourceFlux() {
		// Each time the data flux emits a new resource, we have to send an authorization
		// request to the PDP to transform / filter the resource.
		// In this example there is just one data flux. But even if there were more than
		// one, it would not make much sense to create a multi request with all the
		// resources if only one of them has changed.
		final Flux<TimeScheduleData> schedulerDataFlux = getSchedulerDataFlux();
		final Authentication authentication = SecurityUtils.getAuthentication();
		return schedulerDataFlux.switchMap(
				data -> pep.filterEnforce(authentication, "readSchedulerData", data)
						.subscribeOn(nonUIThread));
	}

}
