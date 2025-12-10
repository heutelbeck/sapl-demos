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
package io.sapl.test.unit.usecase;

import static io.sapl.test.Matchers.any;
import static io.sapl.test.Matchers.args;

import org.junit.jupiter.api.Test;

import io.sapl.api.model.Value;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.libraries.TemporalFunctionLibrary;
import io.sapl.test.SaplTestFixture;

/**
 * Tests for streaming policy evaluation using the thenEmit pattern.
 * The new fixture gives fine-grained control over attribute emissions,
 * eliminating the need for virtual time and duration-based mocking.
 */
class E_PolicyStreamingTest {

    private static final String POLICY = "/policies/policyStreaming.sapl";

    /**
     * Tests streaming attribute emissions using thenEmit pattern.
     * Each emission triggers policy re-evaluation.
     */
    @Test
    void whenStreamingAttributeChanges_thenDecisionUpdates() {
        var timestamp1 = Value.of("2021-02-08T16:16:01.000Z"); // second = 1
        var timestamp2 = Value.of("2021-02-08T16:16:02.000Z"); // second = 2
        var timestamp3 = Value.of("2021-02-08T16:16:03.000Z"); // second = 3
        var timestamp4 = Value.of("2021-02-08T16:16:04.000Z"); // second = 4
        var timestamp5 = Value.of("2021-02-08T16:16:05.000Z"); // second = 5
        var timestamp6 = Value.of("2021-02-08T16:16:06.000Z"); // second = 6

        SaplTestFixture.createSingleTest()
                .withFunctionLibrary(TemporalFunctionLibrary.class)
                .withPolicyFromResource(POLICY)
                .givenEnvironmentAttribute("timeMock", "time.now", args(), timestamp1)
                .whenDecide(AuthorizationSubscription.of("ROLE_DOCTOR", "read", "heartBeatData"))
                .expectNotApplicable()
                .thenEmit("timeMock", timestamp2)
                .expectNotApplicable()
                .thenEmit("timeMock", timestamp3)
                .expectNotApplicable()
                .thenEmit("timeMock", timestamp4)
                .expectNotApplicable()
                .thenEmit("timeMock", timestamp5) // second > 4, now permits
                .expectPermit()
                .thenEmit("timeMock", timestamp6)
                .expectPermit()
                .verify();
    }

    /**
     * Tests function mock that returns different values on consecutive calls.
     */
    @Test
    void whenFunctionReturnsSequence_thenDecisionUpdates() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenEnvironmentAttribute("timeMock", "time.now", args(), Value.of("initial"))
                .givenFunction("time.secondOf", args(any()), Value.of(4), Value.of(5))
                .whenDecide(AuthorizationSubscription.of("ROLE_DOCTOR", "read", "heartBeatData"))
                .expectNotApplicable() // secondOf returns 4, not > 4
                .thenEmit("timeMock", Value.of("updated"))
                .expectPermit() // secondOf returns 5, which is > 4
                .verify();
    }

    /**
     * Tests streaming with multiple values using thenEmit pattern.
     */
    @Test
    void whenMultipleEmissions_thenMultipleDecisions() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenEnvironmentAttribute("timeMock", "time.now", args(), Value.of("t1"))
                .givenFunction("time.secondOf", args(any()), Value.of(3), Value.of(4), Value.of(5))
                .whenDecide(AuthorizationSubscription.of("ROLE_DOCTOR", "read", "heartBeatData"))
                .expectNotApplicable() // 3 <= 4
                .thenEmit("timeMock", Value.of("t2"))
                .expectNotApplicable() // 4 <= 4
                .thenEmit("timeMock", Value.of("t3"))
                .expectPermit() // 5 > 4
                .verify();
    }

}
