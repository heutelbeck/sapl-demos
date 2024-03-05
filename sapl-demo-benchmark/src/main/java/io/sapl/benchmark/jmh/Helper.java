/*
 * Copyright (C) 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.benchmark.jmh;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.util.BenchmarkException;
import io.sapl.benchmark.BenchmarkExecutionContext;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import reactor.core.publisher.Mono;


public class Helper {
    private Helper() {
        throw new IllegalStateException("Utility class");
    }

    public static void decide(PolicyDecisionPoint pdp, AuthorizationSubscription authorizationSubscription){
        var decision = pdp.decide(authorizationSubscription).blockFirst();
        if ( decision == null || decision.getDecision() == null || decision.getDecision() != Decision.PERMIT ){
            throw new BenchmarkException("Invalid decision: " + decision);
        }
    }

    public static void decideOnce(PolicyDecisionPoint pdp, AuthorizationSubscription authorizationSubscription){
        var decision = pdp.decideOnce(authorizationSubscription).block();
        if ( decision == null || decision.getDecision() == null || decision.getDecision() != Decision.PERMIT ){
            throw new BenchmarkException("Invalid decision: " + decision);
        }
    }

    public static ReactiveClientRegistrationRepository getClientRegistrationRepository(BenchmarkExecutionContext config){
        return registrationId -> Mono.just(ClientRegistration
                .withRegistrationId(registrationId)
                .tokenUri(config.getOauth2TokenUri())
                .clientId(config.getOauth2ClientId())
                .clientSecret(config.getOauth2ClientSecret())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(config.getOauth2Scope())
                .build());
    }
}
