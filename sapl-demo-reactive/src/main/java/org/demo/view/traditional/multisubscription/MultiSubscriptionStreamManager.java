package org.demo.view.traditional.multisubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.MultiAuthorizationDecision;
import io.sapl.api.pdp.MultiAuthorizationSubscription;
import io.sapl.spring.pep.PolicyEnforcementPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

/**
 * Manages subscriptions to streams returned by the Policy Enforcement Point for
 * multi-subscriptions. It keeps the subscriptions and updates the authorization decisions
 * for the single authorization subscriptions contained in the multi-subscriptions in the
 * session until {@link #dispose()} is called.
 */
@Slf4j
public class MultiSubscriptionStreamManager {

	private PolicyEnforcementPoint pep;

	private List<Disposable> subscriptions = new ArrayList<>();

	private Set<String> multiSubscriptionIdsWithSubscription = new HashSet<>();

	private Map<String, Decision> decisionsByAuthorizationSubscriptionId = new HashMap<>();

	/**
	 * Creates a new {@code MultiSubscriptionStreamManager} instance delegating to the
	 * given Policy Enforcement Point.
	 * @param pep the Policy Enforcement Point to be used.
	 */
	public MultiSubscriptionStreamManager(PolicyEnforcementPoint pep) {
		this.pep = pep;
	}

	/**
	 * If a subscription to the stream returned by the PEP for a multi-subscription with
	 * the given ID does not yet exist, a new one is created and the first authorization
	 * decisions for each contained single authorization subscription are stored.
	 * @param multiSubscriptionId the ID of the multi-subscription.
	 * @param multiSubscription the multi-subscription for which a subscription has to be
	 * set up.
	 */
	public void setupNewMultiSubscription(String multiSubscriptionId,
			MultiAuthorizationSubscription multiSubscription) {
		if (!hasSubscriptionFor(multiSubscriptionId)) {
			log.debug("setup multi-subscription for multiSubscriptionId {}", multiSubscriptionId);
			final MultiAuthorizationDecision multiAuthzDecision = pep.filterEnforceAll(multiSubscription).blockFirst();
			if (multiAuthzDecision != null) {
				multiAuthzDecision.forEach(ir -> decisionsByAuthorizationSubscriptionId
						.put(ir.getAuthorizationSubscriptionId(), ir.getAuthorizationDecision().getDecision()));
				multiSubscriptionIdsWithSubscription.add(multiSubscriptionId);
			}
			final Disposable subscription = pep.filterEnforce(multiSubscription)
					.subscribe(ir -> decisionsByAuthorizationSubscriptionId.put(ir.getAuthorizationSubscriptionId(),
							ir.getAuthorizationDecision().getDecision()));
			subscriptions.add(subscription);
		}
	}

	/**
	 * Returns {@code true} if a multi-subscription with the given ID has already been
	 * {@link #setupNewMultiSubscription(String, MultiAuthorizationSubscription) set up},
	 * {@code false} otherwise.
	 * @param multiSubscriptionId the ID of the multi-subscription
	 * @return {@code true} if a multi-subscription with the given ID has already been set
	 * up.
	 */
	public boolean hasSubscriptionFor(String multiSubscriptionId) {
		return multiSubscriptionIdsWithSubscription.contains(multiSubscriptionId);
	}

	/**
	 * Returns {@code true} if the current authorization decision for the authorization
	 * subscription with the given ID is PERMIT, {@code false} otherwise.
	 * @param authzSubscriptionId the ID of the single authorization subscription.
	 * @return {@code true} if the current authorization decision for the authorization
	 * subscription with the given ID is PERMIT.
	 */
	public boolean isAccessPermittedForAuthorizationSubscriptionWithId(String authzSubscriptionId) {
		return decisionsByAuthorizationSubscriptionId.get(authzSubscriptionId) == Decision.PERMIT;
	}

	/**
	 * Disposes all subscriptions to single authorization subscription streams returned by
	 * the PEP.
	 */
	public void dispose() {
		log.debug("disposing {} subscriptions", subscriptions.size());
		subscriptions.forEach(Disposable::dispose);
		multiSubscriptionIdsWithSubscription.clear();
		decisionsByAuthorizationSubscriptionId.clear();
	}

}
