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

import io.sapl.api.pdp.AuthorizationSubscription;
import org.junit.jupiter.api.Test;

class APolicySimpleTest {

    private static final String POLICY = "/policies/policySimple.sapl";

    @Test
    void whenForClauseMatchesAndSubjectIsWilliAndActionIsRead_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of("willi", "read", "document"))
                .expectPermit()
                .verify();
    }

    @Test
    void whenForClauseMatchesButSubjectIsNotWilli_thenDeny() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of("notWilli", "read", "document"))
                .expectDeny()
                .verify();
    }

    @Test
    void whenForClauseMatchesButActionIsNotRead_thenDeny() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of("willi", "write", "document"))
                .expectDeny()
                .verify();
    }

    @Test
    void whenForClauseDoesNotMatch_thenNotApplicable() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of("willi", "read", "other-resource"))
                .expectNotApplicable()
                .verify();
    }

}
