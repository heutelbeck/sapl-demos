/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.test.integration.usecase;

import org.junit.jupiter.api.Test;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.test.SaplTestFixture;

/**
 * Integration tests that verify combined policy evaluation and single policy
 * behavior. Demonstrates using both integration and single test modes with the
 * new fixture.
 */
class ASimplePDPTest {

    /**
     * Tests the combined decision from all policies in the policiesIT directory.
     * With permit-overrides combining algorithm, policy_B's PERMIT overrides policy_A's DENY.
     */
    @Test
    void whenEvaluatingCombinedPolicies_thenPermit() {
        SaplTestFixture.createIntegrationTest()
                .withConfigurationFromResources("policiesIT")
                .whenDecide(AuthorizationSubscription.of("WILLI", "read", "foo"))
                .expectPermit()
                .verify();
    }

    /**
     * Tests policy_A in isolation - it should DENY for the given subscription.
     */
    @Test
    void whenEvaluatingSinglePolicyA_thenDeny() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource("/policiesIT/policy_A.sapl")
                .whenDecide(AuthorizationSubscription.of("WILLI", "read", "foo"))
                .expectDeny()
                .verify();
    }

    /**
     * Tests policy_B in isolation - it should PERMIT for the given subscription.
     */
    @Test
    void whenEvaluatingSinglePolicyB_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource("/policiesIT/policy_B.sapl")
                .whenDecide(AuthorizationSubscription.of("WILLI", "read", "foo"))
                .expectPermit()
                .verify();
    }

}
