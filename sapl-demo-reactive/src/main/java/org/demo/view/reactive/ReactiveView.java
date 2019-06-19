package org.demo.view.reactive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.demo.model.SchedulerData;
import org.demo.security.SecurityUtils;
import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.demo.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Response;
import io.sapl.spring.PolicyEnforcementPoint;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringView(name = "reactive")
@SpringComponent("reactiveView")
public class ReactiveView extends AbstractReactiveView {

	private static final long serialVersionUID = 1L;

	private transient PolicyEnforcementPoint pep;

	@Autowired
	public ReactiveView(PolicyEnforcementPoint pep, HeartBeatService heartBeatService,
			BloodPressureService bloodPressureService, ScheduleService scheduleService) {
		super(heartBeatService, bloodPressureService, scheduleService);
		this.pep = pep;
	}

	protected Flux<Object[]> getCombinedFluxForNonFilteredResources() {
		final Authentication authentication = SecurityUtils.getAuthentication();

		final Flux<Decision> heartBeatAccessDecisionFlux = pep
				.enforce(authentication, "read", "heartBeatData")
				.subscribeOn(Schedulers.newElastic("hb-pdp"));
		final Flux<Decision> bloodPressureAccessDecisionFlux = pep
				.enforce(authentication, "read", "bloodPressureData")
				.subscribeOn(Schedulers.newElastic("bp-pdp"));

		return Flux.combineLatest(heartBeatAccessDecisionFlux,
				bloodPressureAccessDecisionFlux, getHeartBeatDataFlux(),
				getDiastolicBloodPressureDataFlux(), getSystolicBloodPressureDataFlux(),
				Function.identity());
	}

	protected void updateUIForNonFilteredResources(Object[] fluxValues) {
		final Decision heartBeatDecision = (Decision) fluxValues[0];
		final Decision bloodPressureDecision = (Decision) fluxValues[1];

		final Integer heartBeat = (Integer) fluxValues[2];
		final Integer diastolic = (Integer) fluxValues[3];
		final Integer systolic = (Integer) fluxValues[4];

		updateUIForNonFilteredResources(heartBeatDecision, bloodPressureDecision,
				heartBeat, diastolic, systolic);
	}

	@Override
	protected Flux<Response> getFilteredResourceFlux() {
		// Each time the data flux emits a new resource, we have to send an authorization
		// request to the PDP to transform / filter the resource.
		// In this example there is just one resource flux. If there where more than one,
		// we would have to execute the following lines of code for each of them.
		final Flux<SchedulerData> schedulerDataFlux = getScheduleDataFlux();
		final Authentication authentication = SecurityUtils.getAuthentication();
		final ExecutorService executorService = Executors.newFixedThreadPool(2);
		return schedulerDataFlux.switchMap(
				data -> pep.filterEnforce(authentication, "readSchedulerData", data)
						.subscribeOn(Schedulers.fromExecutorService(executorService)));
	}

}
