package org.demo.view.reactive;

import java.util.function.Function;

import org.demo.service.BloodPressureService;
import org.demo.service.HeartBeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.Decision;
import io.sapl.pep.SAPLAuthorizer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringComponent("reactiveView")
@SpringView(name = "reactive")
public class ReactiveView extends AbstractReactiveView {

    private final SAPLAuthorizer authorizer;

    @Autowired
    public ReactiveView(SAPLAuthorizer authorizer, HeartBeatService heartBeatService, BloodPressureService bloodPressureService) {
        super(heartBeatService, bloodPressureService);
        this.authorizer = authorizer;
    }

    protected Flux<Object[]> getCombinedFlux() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final Flux<Decision> heartBeatAccessDecisionFlux = authorizer.authorize(authentication, "read", "heartBeatData")
                .subscribeOn(Schedulers.newElastic("hb-pdp"));
        final Flux<Decision> bloodPressureAccessDecisionFlux = authorizer.authorize(authentication, "read", "bloodPressureData")
                .subscribeOn(Schedulers.newElastic("bp-pdp"));

        return Flux.combineLatest(
                heartBeatAccessDecisionFlux,
                bloodPressureAccessDecisionFlux,
                getHeartBeatDataFlux(),
                getDiastolicBloodPressureDataFlux(),
                getSystolicBloodPressureDataFlux(),
                Function.identity());
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
        authorizer.dispose();
        super.beforeLeave(event);
    }
}
