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

import static io.sapl.test.Matchers.isPermit;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.libraries.FilterFunctionLibrary;

/**
 * Tests for complex authorization decision expectations with obligations and
 * transformed resources. Demonstrates the new fixture's fluent API for decision
 * matching.
 */
class GPolicyWithComplexExpectStepTest {

    private static final String POLICY = "/policies/policyWithObligationAndResource.sapl";

    static class SubjectDTO {
        public String name      = "Willi";
        public String authority = "ROLE_ADMIN";
    }

    private final Object subject = new SubjectDTO();

    static class ActionDTO {
        static class JavaDTO {
            public String name = "findById";
        }

        public JavaDTO java = new JavaDTO();
    }

    private final Object action = new ActionDTO();

    static class ResourceDTO {
        public String id            = "56";
        public String diagnosisText = "diagnosisText";
        public String icd11Code     = "icd11Code";
    }

    private final Object resource = new ResourceDTO();

    /**
     * Tests exact match of authorization decision with obligation and transformed resource.
     */
    @Test
    void whenAdminAccessesPatientData_thenPermitWithObligationAndBlackenedResource() {
        var obligation = Value.ofObject(Map.of(
                "type", Value.of("logAccess"),
                "message", Value.of("Willi has accessed patient data (id=56) as an administrator.")));

        var expectedResource = Value.ofObject(Map.of(
                "id", Value.of("56"),
                "diagnosisText", Value.of("█████████████"),
                "icd11Code", Value.of("ic███████")));
        var x = AuthorizationSubscription.of(subject, action, resource);
        System.err.println("->"+x);
        SaplTestFixture.createSingleTest()
                .withFunctionLibrary(FilterFunctionLibrary.class)
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of(subject, action, resource))
                .expectDecisionMatches(isPermit()
                        .containsObligation(obligation)
                        .withResource(expectedResource))
                .verify();
    }

    /**
     * Tests using containsObligationMatching with a custom predicate for more
     * flexible validation.
     */
    @Test
    void whenAdminAccessesPatientData_thenObligationContainsLogAccessType() {
        SaplTestFixture.createSingleTest()
                .withFunctionLibrary(FilterFunctionLibrary.class)
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of(subject, action, resource))
                .expectDecisionMatches(isPermit()
                        .containsObligationMatching(obligationValue -> {
                            if (obligationValue instanceof ObjectValue obj) {
                                var typeField = obj.get("type");
                                return typeField instanceof TextValue typeText
                                        && "logAccess".equals(typeText.value());
                            }
                            return false;
                        }))
                .verify();
    }

    /**
     * Tests combined obligation and resource verification using the fluent matcher API.
     */
    @Test
    void whenAdminAccessesPatientData_thenResourceIsBlackened() {
        var expectedResource = Value.ofObject(Map.of(
                "id", Value.of("56"),
                "diagnosisText", Value.of("█████████████"),
                "icd11Code", Value.of("ic███████")));

        SaplTestFixture.createSingleTest()
                .withFunctionLibrary(FilterFunctionLibrary.class)
                .withPolicyFromResource(POLICY)
                .whenDecide(AuthorizationSubscription.of(subject, action, resource))
                .expectDecisionMatches(isPermit().withResource(expectedResource))
                .verify();
    }

}
