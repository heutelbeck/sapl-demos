package io.sapl.pdp.multitenant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.IdentifiableAuthorizationDecision;
import io.sapl.api.pdp.MultiAuthorizationDecision;
import io.sapl.api.pdp.MultiAuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class MultiTenantAwarePolicyDecisionPoint implements PolicyDecisionPoint {

	private final TenantIdExtractor tenantIdExtractor;

	private final static PolicyDecisionPoint ALWAYS_INDETERMINATE_PDP_DELEGATE = new AlwaysIndeterminatePolicyDecisionPoint();

	private Map<String, PolicyDecisionPoint> tenantToPdpDelegate = new HashMap<>();

	public void loadTenantPolicyDecisionPoint(String tenantId, PolicyDecisionPoint pdp) {
		tenantToPdpDelegate.put(tenantId, pdp);
	}

	@Override
	public Flux<AuthorizationDecision> decide(AuthorizationSubscription authzSubscription) {
		var delegate = getPdpDelegateForCurrentSession();
		return delegate.decide(authzSubscription);
	}

	@Override
	public Flux<IdentifiableAuthorizationDecision> decide(MultiAuthorizationSubscription multiAuthzSubscription) {
		var delegate = getPdpDelegateForCurrentSession();
		return delegate.decide(multiAuthzSubscription);
	}

	@Override
	public Flux<MultiAuthorizationDecision> decideAll(MultiAuthorizationSubscription multiAuthzSubscription) {
		var delegate = getPdpDelegateForCurrentSession();
		return delegate.decideAll(multiAuthzSubscription);
	}

	PolicyDecisionPoint getPdpDelegateForCurrentSession() {
		var tenantId = tenantIdExtractor.tenantOf(SecurityContextHolder.getContext().getAuthentication());
		if (tenantId.isEmpty()) {
			log.warn(
					"Could not determine tenant of current subject. There is either no authenticated subject, or the Principal is not supported by the currently deployed TenantIdExtractor.");
			return ALWAYS_INDETERMINATE_PDP_DELEGATE;
		}
		if (!tenantToPdpDelegate.containsKey(tenantId.get())) {
			log.warn(
					"The subject is from tenant '{}', but no matching PDP is registered. Have you already created a matching folder for the tenants policies and PDP configuration? The folder name must be identical to '{}'.",
					tenantId.get(), tenantId.get());
			return ALWAYS_INDETERMINATE_PDP_DELEGATE;
		}
		log.info("DELEGATING: {}",tenantId.get());
		return tenantToPdpDelegate.get(tenantId.get());
	}
}
