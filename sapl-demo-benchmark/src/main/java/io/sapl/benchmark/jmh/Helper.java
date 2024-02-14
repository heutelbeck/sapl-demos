package io.sapl.benchmark.jmh;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.BenchmarkExecutionContext;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import reactor.core.publisher.Mono;


public class Helper {
    public static void decide(PolicyDecisionPoint pdp, AuthorizationSubscription authorizationSubscription){
        var decision = pdp.decide(authorizationSubscription).blockFirst();
        if ( decision == null || decision.getDecision() == null || decision.getDecision() != Decision.PERMIT ){
            throw new RuntimeException("Invalid decision: " + decision);
        }
    }

    public static void decideOnce(PolicyDecisionPoint pdp, AuthorizationSubscription authorizationSubscription){
        var decision = pdp.decideOnce(authorizationSubscription).block();
        if ( decision == null || decision.getDecision() == null || decision.getDecision() != Decision.PERMIT ){
            throw new RuntimeException("Invalid decision: " + decision);
        }
    }

    public static ReactiveClientRegistrationRepository getClientRegistrationRepository(BenchmarkExecutionContext config){
        return registrationId -> Mono.just(ClientRegistration
                .withRegistrationId(registrationId)
                .tokenUri(config.oauth2_token_uri)
                .clientId(config.oauth2_client_id)
                .clientSecret(config.oauth2_client_secret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(config.oauth2_scope)
                .build());
    }
}
