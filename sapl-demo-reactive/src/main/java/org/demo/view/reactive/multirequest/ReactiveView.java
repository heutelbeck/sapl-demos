package org.demo.view.reactive.multirequest;

import org.demo.security.SecurityUtils;
import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.demo.service.TimeScheduleService;
import org.demo.view.reactive.AbstractReactiveView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.spring.PolicyEnforcementPoint;
import reactor.core.publisher.Flux;

/**
 * Concrete reactive view implementation demonstrating the usage of SAPL multi-requests
 * for controlling access to heart beat and blood pressure data directly updating the
 * frontend upon authorization decision changes.
 */
@SpringView(name = "reactiveMultiRequest")
@SpringComponent("reactiveMultiRequestView")
public class ReactiveView extends AbstractReactiveView {

	private static final long serialVersionUID = 1L;

	private static final String READ_HEART_BEAT_DATA_REQUEST_ID = "readHeartBeatData";

	private static final String READ_BLOOD_PRESSURE_DATA_REQUEST_ID = "readBloodPressureData";

	@Autowired
	public ReactiveView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, TimeScheduleService timeScheduleService) {
		super(pep, heartBeatService, bloodPressureService, timeScheduleService);
	}

	@Override
	protected Flux<NonFilteredResourcesData> getCombinedFluxForNonFilteredResources() {
		final Authentication authentication = SecurityUtils.getAuthentication();

		final MultiRequest multiRequest = new MultiRequest()
				.addRequest(READ_HEART_BEAT_DATA_REQUEST_ID, authentication, "read", "heartBeatData")
				.addRequest(READ_BLOOD_PRESSURE_DATA_REQUEST_ID, authentication, "read", "bloodPressureData");

		final Flux<MultiResponse> accessDecisionFlux = pep.filterEnforceAll(multiRequest).subscribeOn(nonUIThread);

		return Flux.combineLatest(accessDecisionFlux, getHeartBeatDataFlux(), getDiastolicBloodPressureDataFlux(),
				getSystolicBloodPressureDataFlux(), this::getNonFilteredResourcesDataFrom);
	}

	private NonFilteredResourcesData getNonFilteredResourcesDataFrom(Object[] fluxValues) {
		final MultiResponse multiResponse = (MultiResponse) fluxValues[0];
		final NonFilteredResourcesData data = new NonFilteredResourcesData();
		data.heartBeatDecision = multiResponse.getDecisionForRequestWithId(READ_HEART_BEAT_DATA_REQUEST_ID);
		data.bloodPressureDecision = multiResponse.getDecisionForRequestWithId(READ_BLOOD_PRESSURE_DATA_REQUEST_ID);
		data.heartBeat = (Integer) fluxValues[1];
		data.diastolic = (Integer) fluxValues[2];
		data.systolic = (Integer) fluxValues[3];
		return data;
	}

}
