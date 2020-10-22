package org.demo.view.reactive.singlesubscription;

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
import io.sapl.spring.pep.PolicyEnforcementPoint;
import reactor.core.publisher.Flux;

/**
 * Concrete reactive view implementation demonstrating the usage of SAPL single
 * subscriptions for controlling access to heart beat and blood pressure data directly
 * updating the frontend upon authorization decision changes.
 */
@SpringView(name = "reactive")
@SpringComponent("reactiveView")
public class ReactiveView extends AbstractReactiveView {

	private static final long serialVersionUID = 1L;

	@Autowired
	public ReactiveView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, TimeScheduleService timeScheduleService) {
		super(pep, heartBeatService, bloodPressureService, timeScheduleService);
	}

	@Override
	protected Flux<NonFilteredResourcesData> getCombinedFluxForNonFilteredResources() {
		final Authentication authentication = SecurityUtils.getAuthentication();

		final Flux<Decision> heartBeatAccessDecisionFlux = pep.enforce(authentication, "read", "heartBeatData")
				.subscribeOn(nonUIThread);
		final Flux<Decision> bloodPressureAccessDecisionFlux = pep.enforce(authentication, "read", "bloodPressureData")
				.subscribeOn(nonUIThread);

		return Flux.combineLatest(heartBeatAccessDecisionFlux, bloodPressureAccessDecisionFlux, getHeartBeatDataFlux(),
				getDiastolicBloodPressureDataFlux(), getSystolicBloodPressureDataFlux(),
				this::getNonFilteredResourcesDataFrom);
	}

	private NonFilteredResourcesData getNonFilteredResourcesDataFrom(Object[] fluxValues) {
		final NonFilteredResourcesData data = new NonFilteredResourcesData();
		data.heartBeatDecision = (Decision) fluxValues[0];
		data.bloodPressureDecision = (Decision) fluxValues[1];
		data.heartBeat = (Integer) fluxValues[2];
		data.diastolic = (Integer) fluxValues[3];
		data.systolic = (Integer) fluxValues[4];
		return data;
	}

}
