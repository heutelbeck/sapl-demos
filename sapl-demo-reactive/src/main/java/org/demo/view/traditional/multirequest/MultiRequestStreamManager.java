package org.demo.view.traditional.multirequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.MultiResponse;
import io.sapl.spring.PolicyEnforcementPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

/**
 * Manages subscriptions to streams returned by the Policy Enforcement Point
 * for multi-requests. It keeps the subscriptions and updates the decisions
 * for the single requests contained in the multi-requests in the session
 * until {@link #dispose()} is called.
 */
@Slf4j
public class MultiRequestStreamManager {

    private PolicyEnforcementPoint pep;

    private List<Disposable> subscriptions = new ArrayList<>();
    private Set<String> multiRequestIdsWithSubscription = new HashSet<>();
    private Map<String, Decision> decisionsByRequestId = new HashMap<>();

    /**
     * Creates a new {@code MultiRequestStreamManager} instance delegating
     * to the given Policy Enforcement Point.
     *
     * @param pep the Policy Enforcement Point to be used.
     */
    public MultiRequestStreamManager(PolicyEnforcementPoint pep) {
        this.pep = pep;
    }

    /**
     * If a subscription to the stream returned by the PEP for a multi-request with the
     * given ID does not yet exist, a new one is created and the first authorization
     * decisions for each contained single request are stored.
     *
     * @param multiRequestId the ID of the multi-request.
     * @param multiRequest the multi-request for which a subscription has to be set up.
     */
    public void setupNewMultiRequest(String multiRequestId, MultiRequest multiRequest) {
        if (! hasMultiRequestSubscriptionFor(multiRequestId)) {
            LOGGER.debug("setup multi-request subscription for multiRequestId {}", multiRequestId);
            final MultiResponse multiResponse = pep.filterEnforceAll(multiRequest).blockFirst();
            if (multiResponse != null) {
                multiResponse.forEach(ir -> decisionsByRequestId.put(ir.getRequestId(), ir.getResponse().getDecision()));
                multiRequestIdsWithSubscription.add(multiRequestId);
            }
            final Disposable subscription = pep.filterEnforce(multiRequest).subscribe(
                ir -> decisionsByRequestId.put(ir.getRequestId(), ir.getResponse().getDecision())
            );
            subscriptions.add(subscription);
        }
    }

    /**
     * Returns {@code true} if a multi-request with the given ID has already been
     * {@link #setupNewMultiRequest(String, MultiRequest) set up}, {@code false}
     * otherwise.
     *
     * @param multiRequestId the ID of the multi-request
     * @return {@code true} if a multi-request with the given ID has already been set up.
     */
    public boolean hasMultiRequestSubscriptionFor(String multiRequestId) {
        return multiRequestIdsWithSubscription.contains(multiRequestId);
    }

    /**
     * Returns {@code true} if the current authorization decision for the request
     * with the given ID is PERMIT, {@code false} otherwise.
     *
     * @param requestId the ID of the single request.
     * @return {@code true} if the current authorization decision for the request
     *         with the given ID is PERMIT.
     */
    public boolean isAccessPermittedForRequestWithId(String requestId) {
        return decisionsByRequestId.get(requestId) == Decision.PERMIT;
    }

    /**
     * Disposes all subscriptions to single request streams returned by the PEP.
     */
    public void dispose() {
        LOGGER.debug("disposing {} subscriptions", subscriptions.size());
        subscriptions.forEach(Disposable::dispose);
        multiRequestIdsWithSubscription.clear();
        decisionsByRequestId.clear();
    }
}
