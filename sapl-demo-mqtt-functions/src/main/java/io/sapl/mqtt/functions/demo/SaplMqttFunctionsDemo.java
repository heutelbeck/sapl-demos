package io.sapl.mqtt.functions.demo;/*
 * Copyright © 2019-2022 Dominic Heutelbeck (dominic@heutelbeck.com)
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

import ch.qos.logback.classic.Logger;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.interpreter.InitializationException;
import io.sapl.interpreter.functions.MqttFunctions;
import io.sapl.pdp.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.PolicyDecisionPointFactory;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This demo shows how the sapl mqtt function library can be used with an embedded PDP.
 */
public class SaplMqttFunctionsDemo {

    private static final String SUBJECT = "subjectName";
    private static final String ACTION = "actionName";
    private static final Logger logger = (Logger) LoggerFactory.getLogger(SaplMqttFunctionsDemo.class);

    /**
     * Starts a demo of the sapl mqtt function library.
     * @param args the configuration parameters will not be evaluated
     * @throws InitializationException is thrown in case the pdp could not be build
     */
    public static void main(String[] args) throws InitializationException {
        // starting the embedded pdp
        EmbeddedPolicyDecisionPoint pdp = buildPdp();

        // building the authorization subscriptions
        var firstAuthzSubscription = AuthorizationSubscription.of(SUBJECT, ACTION,
                "legalTopic/#");
        var secondAuthzSubscription = AuthorizationSubscription.of(SUBJECT, ACTION,
                "first/second/#");
        var thirdAuthzSubscription = AuthorizationSubscription.of(SUBJECT, ACTION,
                "munich/+/temperature");

        // evaluating the authorization subscriptions
        pdp.decide(firstAuthzSubscription).subscribe(authzDecision ->
                handleAuthorizationDecision(firstAuthzSubscription, authzDecision));
        pdp.decide(secondAuthzSubscription).subscribe(authzDecision ->
                handleAuthorizationDecision(secondAuthzSubscription, authzDecision));
        pdp.decide(thirdAuthzSubscription).subscribe(authzDecision ->
                handleAuthorizationDecision(thirdAuthzSubscription, authzDecision));

        pdp.dispose();
    }

    private static void handleAuthorizationDecision(AuthorizationSubscription authzSubscription,
                                                    AuthorizationDecision authzDecision) {
        logger.info("Decision for authzSubscription '{}': {}",
                authzSubscription, authzDecision.getDecision());
    }

    private static EmbeddedPolicyDecisionPoint buildPdp() throws InitializationException {
        return PolicyDecisionPointFactory
                .filesystemPolicyDecisionPoint("sapl-mqtt-functions-demo/src/main/resources/policies",
                        List.of(), List.of(new MqttFunctions()));
    }
}
