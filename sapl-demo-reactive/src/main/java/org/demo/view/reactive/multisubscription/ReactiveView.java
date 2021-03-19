package org.demo.view.reactive.multisubscription;

import org.demo.security.SecurityUtils;
import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.demo.service.TimeScheduleService;
import org.demo.view.reactive.AbstractReactiveView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.MultiAuthorizationDecision;
import io.sapl.api.pdp.MultiAuthorizationSubscription;
import io.sapl.spring.pep.PolicyEnforcementPoint;
import reactor.core.publisher.Flux;

/**
 * Concrete reactive view implementation demonstrating the usage of SAPL
 * multi-subscriptions for controlling access to heart beat and blood pressure data
 * directly updating the frontend upon authorization decision changes.
 */
@SpringView(name = "reactiveMultiSubscription")
@SpringComponent("reactiveMultiSubscriptionView")
public class ReactiveView extends AbstractReactiveView {

	private static final long serialVersionUID = 1L;

	private static final String READ_HEART_BEAT_DATA_SUBSCRIPTION_ID = "readHeartBeatData";

	private static final String READ_BLOOD_PRESSURE_DATA_SUBSCRIPTION_ID = "readBloodPressureData";

	@Autowired
	public ReactiveView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, TimeScheduleService timeScheduleService) {
		super(pep, heartBeatService, bloodPressureService, timeScheduleService);
	}

	@Override
	protected Flux<NonFilteredResourcesData> getCombinedFluxForNonFilteredResources() {
		final Authentication authentication = SecurityUtils.getAuthentication();

		final MultiAuthorizationSubscription multiSubscription = new MultiAuthorizationSubscription()
				.addAuthorizationSubscription(READ_HEART_BEAT_DATA_SUBSCRIPTION_ID, authentication, "read",
						"heartBeatData")
				.addAuthorizationSubscription(READ_BLOOD_PRESSURE_DATA_SUBSCRIPTION_ID, authentication, "read",
						"bloodPressureData");

		final Flux<MultiAuthorizationDecision> accessDecisionFlux = pep.filterEnforceAll(multiSubscription)
				.subscribeOn(nonUIThread);

		return Flux.combineLatest(accessDecisionFlux, getHeartBeatDataFlux(), getDiastolicBloodPressureDataFlux(),
				getSystolicBloodPressureDataFlux(), this::getNonFilteredResourcesDataFrom);
	}

	private NonFilteredResourcesData getNonFilteredResourcesDataFrom(Object[] fluxValues) {
		final MultiAuthorizationDecision multiDecision = (MultiAuthorizationDecision) fluxValues[0];
		final NonFilteredResourcesData data = new NonFilteredResourcesData();
		data.heartBeatDecision = multiDecision.getDecisionForSubscriptionWithId(READ_HEART_BEAT_DATA_SUBSCRIPTION_ID);
		data.bloodPressureDecision = multiDecision
				.getDecisionForSubscriptionWithId(READ_BLOOD_PRESSURE_DATA_SUBSCRIPTION_ID);
		data.heartBeat = (Integer) fluxValues[1];
		data.diastolic = (Integer) fluxValues[2];
		data.systolic = (Integer) fluxValues[3];
		return data;
	}

}
