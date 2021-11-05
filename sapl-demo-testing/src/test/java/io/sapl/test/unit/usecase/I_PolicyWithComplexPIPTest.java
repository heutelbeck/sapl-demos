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