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
import io.sapl.test.SaplTestFixture;

class D_PolicyWithMultipleFunctionsOrPIPsTest {

    private static final String POLICY = "/policies/policyWithMultipleFunctionsOrPIPs.sapl";

    @Test
    void whenMockingMultipleFunctionsAndAttributes_thenPermit() {
        SaplTestFixture.createSingleTest()
                .withPolicyFromResource(POLICY)
                .givenAttribute("upperMock", "test.upper", any(), args(), Value.of("WILLI"))
                .givenEnvironmentAttribute("timeMock", "time.now", args(), Value.of("2021-02-08T16:16:33.616Z"))
                .givenFunction("time.dayOfWeekFrom", args(any()), Value.of("SATURDAY"))
                .whenDecide(AuthorizationSubscription.of("willi", "read", "something"))
                .expectPermit()
                .verify();
    }

}
