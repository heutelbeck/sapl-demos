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
package io.sapl.test;

import static io.sapl.test.Matchers.any;
import static io.sapl.test.Matchers.args;

import org.junit.jupiter.api.Test;

import io.sapl.api.model.Value;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.libraries.TemporalFunctionLibrary;

/**
 * Streaming tests with the new fixture using thenEmit pattern.
 * The new fixture provides fine-grained control over attribute emissions,
 * eliminating the need for virtual time and duration-based mocking.
 */
class KStreamingVirtualTimeTest {

    /**
     * Tests streaming with explicit attribute emissions using thenEmit.
     * The policy permits when time.secondOf(time.now) >= 4.
     */
    @Test
    void whenTimeNowEmitsSequence_thenDecisionUpdatesAccordingly() {
        var timestamp1 = Value.of("2021-02-08T16:16:01.000Z"); // second = 1
        var timestamp2 = Value.of("2021-02-08T16:16:02.000Z"); // second = 2
        var timestamp3 = Value.of("2021-02-08T16:16:03.000Z"); // second = 3
        var timestamp4 = Value.of("2021-02-08T16:16:04.000Z"); // second = 4
        var timestamp5 = Value.of("2021-02-08T16:16:05.000Z"); // second = 5
        var timestamp6 = Value.of("2021-02-08T16:16:06.000Z"); // second = 6

        SaplTestFixture.createIntegrationTest()
                .withFunctionLibrary(TemporalFunctionLibrary.class)
                .withConfigurationFromResources("policiesIT")
                .givenEnvironmentAttribute("timeMock", "time.now", args(any()), timestamp1)
                .whenDecide(AuthorizationSubscription.of("WILLI", "read", "bar"))
                .expectDeny()                                // second 1 < 4, policy_C not applicable, default deny
                .thenEmit("timeMock", timestamp2)
                .expectDeny()                                // second 2 < 4
                .thenEmit("timeMock", timestamp3)
                .expectDeny()                                // second 3 < 4
                .thenEmit("timeMock", timestamp4)
                .expectPermit()                              // second 4 >= 4, permit!
                .thenEmit("timeMock", timestamp5)
                .expectPermit()                              // second 5 >= 4
                .thenEmit("timeMock", timestamp6)
                .expectPermit()                              // second 6 >= 4
                .verify();
    }

    /**
     * Tests streaming with mocked function returning a sequence of values.
     * Each emission of time.now triggers re-evaluation with the next function result.
     */
    @Test
    void whenFunctionReturnsSequence_thenDecisionUpdates() {
        SaplTestFixture.createIntegrationTest()
                .withConfigurationFromResources("policiesIT")
                .givenEnvironmentAttribute("timeMock", "time.now", args(any()), Value.of("t1"))
                // time.secondOf returns sequence: 3, 4
                .givenFunction("time.secondOf", args(any()), Value.of(3), Value.of(4))
                .whenDecide(AuthorizationSubscription.of("WILLI", "read", "bar"))
                .expectDeny()                                // 3 < 4, not applicable, default deny
                .thenEmit("timeMock", Value.of("t2"))
                .expectPermit()                              // 4 >= 4, permit
                .verify();
    }

}
