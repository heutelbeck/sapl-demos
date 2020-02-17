package org.demo.pep;

import java.util.function.BiConsumer;

import com.vaadin.flow.component.UI;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import reactor.core.Disposable;

public class VaadinPEP<Component> {
	private PolicyDecisionPoint pdp;

	private AuthorizationSubscription subscription;

	private Component component;

	private UI ui;

	private BiConsumer<Component, AuthorizationDecision> permitListener;

	private BiConsumer<Component, AuthorizationDecision> denyListener;

	private BiConsumer<Component, AuthorizationDecision> notApplicableListener;

	private BiConsumer<Component, AuthorizationDecision> indeterminateListener;

	private Disposable decisionFlux;

	public VaadinPEP(Component component, AuthorizationSubscription subscription, PolicyDecisionPoint pdp, UI ui) {
		this.component = component;
		this.pdp = pdp;
		this.subscription = subscription;
		this.ui = ui;
	}

	public void newSub(AuthorizationSubscription subscription) {
		this.subscription = subscription;
	}

	public void enforce() {
		this.decisionFlux = pdp.decide(subscription).subscribe(this::onDecision);
	}

	private void onDecision(AuthorizationDecision decision) {
		if (decision.getDecision() == Decision.PERMIT) {
			ui.access(() -> permitListener.accept(component, decision));
		}
		else if (decision.getDecision() == Decision.DENY) {
			ui.access(() -> denyListener.accept(component, decision));
		}
		else if (decision.getDecision() == Decision.INDETERMINATE) {
			if (indeterminateListener != null) {
				ui.access(() -> indeterminateListener.accept(component, decision));
			}
			else {
				ui.access(() -> denyListener.accept(component, decision));
			}
		}
		else if (decision.getDecision() == Decision.DENY) {
			if (notApplicableListener != null) {
				ui.access(() -> notApplicableListener.accept(component, decision));
			}
			else {
				ui.access(() -> denyListener.accept(component, decision));
			}
		}
	}

	public void dispose() {
		this.decisionFlux.dispose();
	}

	public void onPermit(BiConsumer<Component, AuthorizationDecision> permitListener) {
		this.permitListener = permitListener;
	}

	public void onDeny(BiConsumer<Component, AuthorizationDecision> denyListener) {
		this.denyListener = denyListener;
	}

	public void onIndeterminate(BiConsumer<Component, AuthorizationDecision> indeterminateListener) {
		this.indeterminateListener = indeterminateListener;
	}

	public void OnNotApplicable(BiConsumer<Component, AuthorizationDecision> notApplicableListener) {
		this.notApplicableListener = notApplicableListener;
	}

}
