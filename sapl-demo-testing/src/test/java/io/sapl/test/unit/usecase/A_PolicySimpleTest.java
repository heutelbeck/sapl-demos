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

import org.junit.jupiter.api.Test;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.test.SaplTestFixture;

class A_PolicySimpleTest {

    @Test
    void whenSubjectIsWilliAndActionIsRead_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource("/policies/policySimple.sapl")
                .whenDecide(AuthorizationSubscription.of("willi", "read", "something"))
                .expectPermit()
                .verify();
    }

    @Test
    void whenSubjectIsNotWilli_thenDeny() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource("/policies/policySimple.sapl")
                .whenDecide(AuthorizationSubscription.of("notWilli", "read", "something"))
                .expectDeny()
                .verify();
    }

    @Test
    void whenActionIsNotRead_thenDeny() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource("/policies/policySimple.sapl")
                .whenDecide(AuthorizationSubscription.of("willi", "write", "something"))
                .expectDeny()
                .verify();
    }

}
