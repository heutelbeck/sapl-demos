/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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

import io.sapl.api.model.Value;
import io.sapl.api.pdp.AuthorizationSubscription;
import org.junit.jupiter.api.Test;

import static io.sapl.test.Matchers.*;

/**
 * Tests for complex function mocking with parameter-specific return values
 * and dynamic attribute emissions.
 */
class FPolicyWithComplexFunctionTest {

    private static final String POLICY = "/policies/policyWithComplexFunction.sapl";

    /**
     * Tests function mock with parameter-specific return values combined with
     * dynamic attribute emissions using thenEmit pattern.
     */
    @Test
    void whenFunctionReturnsDependsOnParameters_andAttributesChange_thenDecisionUpdates() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenEnvironmentAttribute("pip1Mock", "company.pip1", args())
                .givenEnvironmentAttribute("pip2Mock", "company.pip2", args())
                // Function returns true when params are (1, "foo")
                .givenFunction("company.complexFunction", args(eq(Value.of(1)), eq(Value.of("foo"))), Value.TRUE)
                // Function returns true when first param is 2 (any second)
                .givenFunction("company.complexFunction", args(eq(Value.of(2)), any()), Value.TRUE)
                // Default: function returns false for any other params
                .givenFunction("company.complexFunction", args(any(), any()), Value.FALSE)
                .whenDecide(AuthorizationSubscription.of("User1", "read", "heartBeatData"))
                // Emit pip1=1, pip2="foo" -> complexFunction(1,"foo")=true -> permit
                .thenEmit("pip1Mock", Value.of(1))
                .thenEmit("pip2Mock", Value.of("foo"))
                .expectPermit()
                // Emit pip2="bar" -> complexFunction(1,"bar")=false -> not applicable
                .thenEmit("pip2Mock", Value.of("bar"))
                .expectNotApplicable()
                // Emit pip1=2 -> complexFunction(2,"bar")=true -> permit
                .thenEmit("pip1Mock", Value.of(2))
                .expectPermit()
                // Emit pip2="xxx" -> complexFunction(2,"xxx")=true -> permit
                .thenEmit("pip2Mock", Value.of("xxx"))
                .expectPermit()
                // Emit pip1=3 -> complexFunction(3,"xxx")=false -> not applicable
                .thenEmit("pip1Mock", Value.of(3))
                .expectNotApplicable()
                .verify();
    }

    /**
     * Tests function mock with fixed return values for specific parameter combinations.
     * Uses parameter-specific mocking to simulate modulo computation.
     */
    @Test
    void whenFunctionHasParameterSpecificMocks_thenDecisionUpdates() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenEnvironmentAttribute("pip1Mock", "company.pip1", args())
                .givenEnvironmentAttribute("pip2Mock", "company.pip2", args())
                // 3 % 2 != 0 -> false
                .givenFunction("company.complexFunction", args(eq(Value.of(3)), eq(Value.of(2))), Value.FALSE)
                // 4 % 2 == 0 -> true
                .givenFunction("company.complexFunction", args(eq(Value.of(4)), eq(Value.of(2))), Value.TRUE)
                .whenDecide(AuthorizationSubscription.of("ROLE_DOCTOR", "read", "heartBeatData"))
                // Emit pip1=3, pip2=2 -> false -> not applicable
                .thenEmit("pip1Mock", Value.of(3))
                .thenEmit("pip2Mock", Value.of(2))
                .expectNotApplicable()
                // Emit pip1=4 -> true -> permit
                .thenEmit("pip1Mock", Value.of(4))
                .expectPermit()
                .verify();
    }

}
