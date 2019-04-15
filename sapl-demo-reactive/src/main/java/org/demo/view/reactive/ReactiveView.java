package org.demo.view.reactive;

import java.util.function.Function;

import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.Request;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringView(name = "reactive")
@SpringComponent("reactiveView")
public class ReactiveView extends AbstractReactiveView {

	private final PolicyDecisionPoint pdp;

	@Autowired
	public ReactiveView(PolicyDecisionPoint pdp, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService) {
		super(heartBeatService, bloodPressureService);
		this.pdp = pdp;
	}

	private static final Request buildRequest(Object subject, Object action, Object resource) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return new Request(mapper.valueToTree(subject), mapper.valueToTree(action), mapper.valueToTree(resource), null);
	}

	protected Flux<Object[]> getCombinedFlux() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		final Flux<Decision> heartBeatAccessDecisionFlux = pdp
				.decide(buildRequest(authentication, "read", "heartBeatData"))
				.subscribeOn(Schedulers.newElastic("hb-pdp")).map(result -> result.getDecision());
		final Flux<Decision> bloodPressureAccessDecisionFlux = pdp
				.decide(buildRequest(authentication, "read", "bloodPressureData"))
				.subscribeOn(Schedulers.newElastic("bp-pdp")).map(result -> result.getDecision());

		return Flux.combineLatest(heartBeatAccessDecisionFlux, bloodPressureAccessDecisionFlux, getHeartBeatDataFlux(),
				getDiastolicBloodPressureDataFlux(), getSystolicBloodPressureDataFlux(), Function.identity());
	}

	protected void updateUI(Object[] fluxValues) {
		final Decision heartBeatDecision = (Decision) fluxValues[0];
		final Decision bloodPressureDecision = (Decision) fluxValues[1];

		final Integer heartBeat = (Integer) fluxValues[2];
		final Integer diastolic = (Integer) fluxValues[3];
		final Integer systolic = (Integer) fluxValues[4];

		updateUI(heartBeatDecision, bloodPressureDecision, heartBeat, diastolic, systolic);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		super.beforeLeave(event);
	}
}
