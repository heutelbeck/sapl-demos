package org.demo.view.reactive.multirequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

	private final Map<String, Decision> accessDecisions;

	@Autowired
	public ReactiveView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, TimeScheduleService timeScheduleService) {
		super(pep, heartBeatService, bloodPressureService, timeScheduleService);
		accessDecisions = new HashMap<>();
		accessDecisions.put(READ_HEART_BEAT_DATA_REQUEST_ID, Decision.DENY);
		accessDecisions.put(READ_BLOOD_PRESSURE_DATA_REQUEST_ID, Decision.DENY);
	}

	@Override
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

	@Override
	protected NonFilteredResourcesData getNonFilteredResourcesDataFrom(Object[] fluxValues) {
		final MultiResponse multiResponse = (MultiResponse) fluxValues[0];
		for (IdentifiableResponse identifiableResponse : multiResponse) {
			accessDecisions.put(identifiableResponse.getRequestId(),
					identifiableResponse.getResponse().getDecision());
		}

		final NonFilteredResourcesData data = new NonFilteredResourcesData();
		data.heartBeatDecision = accessDecisions.get(READ_HEART_BEAT_DATA_REQUEST_ID);
		data.bloodPressureDecision = accessDecisions.get(READ_BLOOD_PRESSURE_DATA_REQUEST_ID);
		data.heartBeat = (Integer) fluxValues[1];
		data.diastolic = (Integer) fluxValues[2];
		data.systolic = (Integer) fluxValues[3];
		return data;
	}

}
