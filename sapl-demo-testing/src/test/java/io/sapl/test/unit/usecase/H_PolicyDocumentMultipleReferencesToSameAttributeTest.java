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

import static io.sapl.test.DecisionMatchers.isPermit;
import static io.sapl.test.Matchers.args;
import static io.sapl.test.Matchers.eq;

import org.junit.jupiter.api.Test;

import io.sapl.api.model.Value;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.test.SaplTestFixture;

/**
 * Tests for policy documents that call the same attribute multiple times.
 * Uses the thenEmit pattern to control attribute emissions and function
 * mocking to control secondOf results.
 */
class H_PolicyDocumentMultipleReferencesToSameAttributeTest {

    private static final String POLICY = "/policies/policyDocumentWithMultipleCallsToSameAttribute.sapl";

    /**
     * Tests streaming attribute with parameter-specific function mocks.
     * Each emission of time.now triggers re-evaluation with the mocked secondOf result.
     */
    @Test
    void whenTimeNowEmitsValues_thenObligationDependsOnSecond() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenEnvironmentAttribute("timeMock", "time.now", args(), Value.of(1))
                // second < 20 -> policy 1 (obligation A)
                .givenFunction("time.secondOf", args(eq(Value.of(1))), Value.of(1))
                .givenFunction("time.secondOf", args(eq(Value.of(2))), Value.of(15))
                // second < 40 -> policy 2 (obligation B)
                .givenFunction("time.secondOf", args(eq(Value.of(3))), Value.of(25))
                .givenFunction("time.secondOf", args(eq(Value.of(4))), Value.of(35))
                // second < 60 -> policy 3 (obligation C)
                .givenFunction("time.secondOf", args(eq(Value.of(5))), Value.of(45))
                .givenFunction("time.secondOf", args(eq(Value.of(6))), Value.of(55))
                .whenDecide(AuthorizationSubscription.of("WILLI", "read", "something"))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("A")))
                .thenEmit("timeMock", Value.of(2))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("A")))
                .thenEmit("timeMock", Value.of(3))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("B")))
                .thenEmit("timeMock", Value.of(4))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("B")))
                .thenEmit("timeMock", Value.of(5))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("C")))
                .thenEmit("timeMock", Value.of(6))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("C")))
                .verify();
    }

    /**
     * Tests streaming with parameter-specific function mocks.
     * Each emission value maps to a specific secondOf result, ensuring consistent
     * behavior regardless of how many policies evaluate the function.
     */
    @Test
    void whenFunctionReturnsSequence_thenObligationChanges() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenEnvironmentAttribute("timeMock", "time.now", args(), Value.of("t1"))
                // Mock by parameter value - each emission maps to a specific second
                // t1, t2 -> second < 20 -> policy 1 (obligation A)
                .givenFunction("time.secondOf", args(eq(Value.of("t1"))), Value.of(1))
                .givenFunction("time.secondOf", args(eq(Value.of("t2"))), Value.of(15))
                // t3, t4 -> second 20-39 -> policy 2 (obligation B)
                .givenFunction("time.secondOf", args(eq(Value.of("t3"))), Value.of(25))
                .givenFunction("time.secondOf", args(eq(Value.of("t4"))), Value.of(35))
                // t5, t6 -> second 40-59 -> policy 3 (obligation C)
                .givenFunction("time.secondOf", args(eq(Value.of("t5"))), Value.of(45))
                .givenFunction("time.secondOf", args(eq(Value.of("t6"))), Value.of(55))
                .whenDecide(AuthorizationSubscription.of("WILLI", "read", "something"))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("A")))
                .thenEmit("timeMock", Value.of("t2"))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("A")))
                .thenEmit("timeMock", Value.of("t3"))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("B")))
                .thenEmit("timeMock", Value.of("t4"))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("B")))
                .thenEmit("timeMock", Value.of("t5"))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("C")))
                .thenEmit("timeMock", Value.of("t6"))
                .expectDecisionMatches(isPermit().containsObligation(Value.of("C")))
                .verify();
    }

}
