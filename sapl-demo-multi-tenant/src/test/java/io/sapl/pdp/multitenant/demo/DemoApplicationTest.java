package io.sapl.pdp.multitenant.demo;
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import io.sapl.pdp.multitenenat.demo.MultiTenantDemoApplication;
import io.sapl.pdp.multitenenat.demo.config.TenantAwareUserDetailsService;
import io.sapl.pdp.multitenenat.demo.controller.PatientsController;
import io.sapl.pdp.multitenenat.demo.domain.DemoData;

@DirtiesContext
@SpringBootTest(classes = MultiTenantDemoApplication.class)
class DemoApplicationTest {

	private static record TestCase(String username, String icd11Code, String diagnosisText) {
	}

	static Collection<TestCase> testCases() {
		return List.of(new TestCase("horsta", "DA♡♡♡♡♡♡♡♡♡♡♡♡", "♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡♡"),
				new TestCase("horstb", "DA████████████", "██████████████████████████████████████"));
	}

	@Autowired
	TenantAwareUserDetailsService userDetails;

	@Autowired
	PatientsController controller;

	@Test
	void contextLoads(ApplicationContext context) {
		assertThat(context).isNotNull();
	}

	@ParameterizedTest
	@MethodSource("testCases")
	void policyEnforcement(TestCase testCase) {
		setAuthentication(testCase.username());
		var optPatient = controller.patient(1L);
		assertThat(optPatient.isPresent()).isTrue();
		var patient = optPatient.get();
		
		assertThat(patient.getId()).isEqualTo(1L);
		assertThat(patient.getMedicalRecordNumber()).isEqualTo("123456");
		assertThat(patient.getName()).isEqualTo("Lenny");
		assertThat(patient.getIcd11Code()).isEqualTo(testCase.icd11Code());
		assertThat(patient.getDiagnosisText()).isEqualTo(testCase.diagnosisText());
		assertThat(patient.getAttendingDoctor()).isEqualTo("Julia");
		assertThat(patient.getAttendingNurse()).isEqualTo("Thomas");
		assertThat(patient.getPhoneNumber()).isEqualTo("+78(0)456-789");
		assertThat(patient.getRoomNumber()).isEqualTo("A.3.47");
	}

	private void setAuthentication(String username) {
		var user = userDetails.loadUserByUsername(username);
		var authn = new UsernamePasswordAuthenticationToken(user, DemoData.DEFAULT_RAW_PASSWORD, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authn);
	}
}
