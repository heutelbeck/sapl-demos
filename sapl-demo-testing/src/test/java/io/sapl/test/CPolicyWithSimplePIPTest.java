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
import static io.sapl.test.Matchers.eq;

import org.junit.jupiter.api.Test;

import io.sapl.api.model.Value;
import io.sapl.api.pdp.AuthorizationSubscription;

class CPolicyWithSimplePIPTest {

    private static final String POLICY = "/policies/policyWithSimplePIP.sapl";

    @Test
    void whenMockingAttributeWithAnyEntity_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenAttribute("upperMock", "test.upper", any(), args(), Value.of("WILLI"))
                .whenDecide(AuthorizationSubscription.of("willi", "read", "something"))
                .expectPermit()
                .verify();
    }

    @Test
    void whenUsingRealPIP_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyInformationPoint(new TestPIP())
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of("willi", "read", "something"))
                .expectPermit()
                .verify();
    }

    @Test
    void whenMockingAttributeWithSpecificEntity_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenAttribute("upperMock", "test.upper", eq(Value.of("willi")), args(), Value.of("WILLI"))
                .whenDecide(AuthorizationSubscription.of("willi", "read", "something"))
                .expectPermit()
                .verify();
    }

}
