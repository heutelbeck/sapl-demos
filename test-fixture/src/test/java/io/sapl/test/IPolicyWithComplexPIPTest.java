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

import static io.sapl.test.Matchers.args;
import static io.sapl.test.Matchers.eq;

import org.junit.jupiter.api.Test;

import io.sapl.api.model.Value;
import io.sapl.api.pdp.AuthorizationSubscription;

/**
 * Tests for complex PIP with attribute having entity value and arguments.
 * The policy uses: parentValue.<pip.attributeWithParams(<pip.attribute1>,
 * <pip.attribute2>)> This means pip.attributeWithParams takes the result of
 * pip.attribute1 and pip.attribute2 as arguments, and uses parentValue (true)
 * as the entity.
 */
class IPolicyWithComplexPIPTest {

    private static final String POLICY = "/policies/policyWithComplexPIP.sapl";

    /**
     * Tests complex PIP where attributeWithParams depends on attribute1 and
     * attribute2. The policy permits when attributeWithParams returns true. The
     * attribute is called with entity=true and the current values of attribute1 and
     * attribute2 as arguments.
     */
    @Test
    void whenAttributeWithParamsReturnsTrue_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                // pip.attribute1 and pip.attribute2 are environment attributes
                .givenEnvironmentAttribute("attr1Mock", "pip.attribute1", args())
                .givenEnvironmentAttribute("attr2Mock", "pip.attribute2", args())
                // pip.attributeWithParams with entity=true and args (2, 2) returns true
                .givenAttribute("attrParamsMock22", "pip.attributeWithParams", eq(Value.TRUE),
                        args(eq(Value.of(2)), eq(Value.of(2))), Value.TRUE)
                // pip.attributeWithParams with entity=true and args (2, 1) returns false
                .givenAttribute("attrParamsMock21", "pip.attributeWithParams", eq(Value.TRUE),
                        args(eq(Value.of(2)), eq(Value.of(1))), Value.FALSE)
                // pip.attributeWithParams with entity=true and args (1, 2) returns false
                .givenAttribute("attrParamsMock12", "pip.attributeWithParams", eq(Value.TRUE),
                        args(eq(Value.of(1)), eq(Value.of(2))), Value.FALSE)
                .whenDecide(AuthorizationSubscription.of("willi", "read", "something"))
                // Emit attr1=1, attr2=2 -> attributeWithParams(true, 1, 2) = false -> not applicable
                .thenEmit("attr1Mock", Value.of(1))
                .thenEmit("attr2Mock", Value.of(2))
                .expectNotApplicable()
                // Emit attr1=2 -> attributeWithParams(true, 2, 2) = true -> permit
                .thenEmit("attr1Mock", Value.of(2))
                .expectPermit()
                // Emit attr2=1 -> attributeWithParams(true, 2, 1) = false -> not applicable
                .thenEmit("attr2Mock", Value.of(1))
                .expectNotApplicable()
                .verify();
    }

}
