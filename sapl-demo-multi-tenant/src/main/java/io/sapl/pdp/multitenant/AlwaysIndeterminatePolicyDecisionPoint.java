package io.sapl.pdp.multitenant;

import java.util.ArrayList;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.IdentifiableAuthorizationDecision;
import io.sapl.api.pdp.MultiAuthorizationDecision;
import io.sapl.api.pdp.MultiAuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import reactor.core.publisher.Flux;

public class AlwaysIndeterminatePolicyDecisionPoint implements PolicyDecisionPoint {

	@Override
	public Flux<AuthorizationDecision> decide(AuthorizationSubscription authzSubscription) {
		return Flux.just(AuthorizationDecision.INDETERMINATE);
	}

	@Override
	public Flux<IdentifiableAuthorizationDecision> decide(MultiAuthorizationSubscription multiAuthzSubscription) {
		var decisions = new ArrayList<IdentifiableAuthorizationDecision>(
				multiAuthzSubscription.getAuthorizationSubscriptions().size());
		for (var entry : multiAuthzSubscription.getAuthorizationSubscriptions().entrySet())
			decisions.add(new IdentifiableAuthorizationDecision(entry.getKey(), AuthorizationDecision.INDETERMINATE));
		return Flux.fromIterable(decisions);
	}

	@Override
	public Flux<MultiAuthorizationDecision> decideAll(MultiAuthorizationSubscription multiAuthzSubscription) {

		var multiDecision = new MultiAuthorizationDecision();
		for (var entry : multiAuthzSubscription.getAuthorizationSubscriptions().entrySet())
			multiDecision.setAuthorizationDecisionForSubscriptionWithId(entry.getKey(),
					AuthorizationDecision.INDETERMINATE);
		return Flux.just(multiDecision);
	}

}
