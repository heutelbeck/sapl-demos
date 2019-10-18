package org.demo.view.traditional.singlesubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.demo.security.SecurityUtils;
import org.springframework.security.core.Authentication;

import io.sapl.api.pdp.Decision;
import io.sapl.spring.PolicyEnforcementPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

/**
 * Manages subscriptions to streams returned by the Policy Enforcement Point for single
 * authorization subscriptions. It keeps the subscriptions and updates the decisions in the session until
 * {@link #dispose()} is called.
 */
@Slf4j
public class SingleSubscriptionStreamManager {

	private PolicyEnforcementPoint pep;

	private List<Disposable> subscriptions = new ArrayList<>();

	private Set<String> keysWithSubscription = new HashSet<>();

	private Map<String, Decision> decisionsByKey = new HashMap<>();

	/**
	 * Creates a new {@code SingleSubscriptionStreamManager} instance delegating to the given
	 * Policy Enforcement Point.
	 * @param pep the Policy Enforcement Point to be used.
	 */
	public SingleSubscriptionStreamManager(PolicyEnforcementPoint pep) {
		this.pep = pep;
	}

	/**
	 * Returns {@code true} if the current authorization decision for the authorization subscription
	 * containing the current Spring authentication as its subject and then given action
	 * and resource is PERMIT, {@code false} otherwise. If a subscription to the stream
	 * returned by the PEP for this authorization subscription does not yet exist, a new one is created.
	 * @param action the action of the authorization subscription
	 * @param resource the resource of the authorization subscription
	 * @return {@code true} if the current authorization decision for the authorization subscription is
	 * PERMIT, {@code false} otherwise.
	 */
	public boolean isAccessPermitted(Object action, Object resource) {
		return isAccessPermitted(action, resource, null);
	}

	/**
	 * Returns {@code true} if the current authorization decision for the authorization subscription
	 * containing the current Spring authentication as its subject and then given action,
	 * resource and environment is PERMIT, {@code false} otherwise. If a subscription to
	 * the stream returned by the PEP for this authorization subscription does not yet exist, a new one is
	 * created.
	 * @param action the action of the authorization subscription
	 * @param resource the resource of the authorization subscription
	 * @param environment the environment of the authorization subscription
	 * @return {@code true} if the current authorization decision for the authorization subscription is
	 * PERMIT, {@code false} otherwise.
	 */
	public boolean isAccessPermitted(Object action, Object resource, Object environment) {
		final String key = createKeyFor(action, resource, environment);
		if (keysWithSubscription.add(key)) {
			LOGGER.debug("setup subscription for action {}, resource {} and environment {}", action, resource,
					environment);
			final Authentication subject = SecurityUtils.getAuthentication();
			final Decision initialDecision = pep.enforce(subject, action, resource, environment).blockFirst();
			decisionsByKey.put(key, initialDecision);
			final Disposable subscription = pep.enforce(subject, action, resource, environment)
					.subscribe(decision -> decisionsByKey.put(key, decision));
			subscriptions.add(subscription);
		}
		return decisionsByKey.get(key) == Decision.PERMIT;
	}

	private String createKeyFor(Object action, Object resource, Object environment) {
		// @formatter:off
		return (action != null ? action.toString() : "null") + "_"
                + (resource != null ? resource.toString() : "null") + "_"
                + (environment != null ? environment.toString() : "null");
		// @formatter:on
	}

	/**
	 * Disposes all subscriptions to single authorization subscription streams returned by the PEP.
	 */
	public void dispose() {
		LOGGER.debug("disposing {} subscriptions", subscriptions.size());
		subscriptions.forEach(Disposable::dispose);
		keysWithSubscription.clear();
		decisionsByKey.clear();
	}

}
