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

import static io.sapl.hamcrest.Matchers.val;
import static io.sapl.test.Imports.arguments;
import static io.sapl.test.Imports.parentValue;
import static io.sapl.test.Imports.thenReturn;
import static io.sapl.test.Imports.whenAttributeParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.unit.SaplUnitTestFixture;

public class I_PolicyWithComplexPIPTest {
	
	private SaplTestFixture fixture;
	
	@BeforeEach
	void setUp() {
		fixture = new SaplUnitTestFixture("policyWithComplexPIP.sapl");
	}
	
	@Test
	void test_policyWithSimpleMockedPIP() {
		
		fixture.constructTestCaseWithMocks()
	    	.givenAttribute("pip.attribute1")
	    	.givenAttribute("pip.attribute2")
	    	.givenAttribute("pip.attributeWithParams", whenAttributeParams(parentValue(val(true)), arguments(val(2), val(2))), thenReturn(Val.of(true)))
	    	.givenAttribute("pip.attributeWithParams", whenAttributeParams(parentValue(val(true)), arguments(val(2), val(1))), thenReturn(Val.of(false)))
	    	.givenAttribute("pip.attributeWithParams", whenAttributeParams(parentValue(val(true)), arguments(val(1), val(2))), thenReturn(Val.of(false)))
	        .when(AuthorizationSubscription.of("willi", "read", "something"))
	        .thenAttribute("pip.attribute1", Val.of(1))
	        .thenAttribute("pip.attribute2", Val.of(2))
	        .expectNextNotApplicable()
	        .thenAttribute("pip.attribute1", Val.of(2))
	        .expectNextPermit()
	        .thenAttribute("pip.attribute2", Val.of(1))
	        .expectNextNotApplicable()
	        .verify();
	}
}