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
package io.sapl.pdp.multitenenat.demo.domain;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.sapl.pdp.multitenenat.demo.config.TenantAwareUserDetailsService;
import io.sapl.pdp.multitenenat.demo.tenants.TenantAwareUserDetails;
import lombok.RequiredArgsConstructor;

/**
 * This class is for loading a demo data set into the database. The run method
 * is executed after the application context is loaded.
 */
@Component
@RequiredArgsConstructor
public class DemoData implements CommandLineRunner {

	// Demo Data Strings
	
	public static final String DEFAULT_RAW_PASSWORD = "password";

	private static final String ROLE_DOCTOR = "ROLE_DOCTOR";

	private static final String ROLE_NURSE = "ROLE_NURSE";

	private static final String ROLE_VISITOR = "ROLE_VISITOR";

	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	private static final String NAME_DOMINIC = "Dominic";

	private static final String NAME_JULIA = "Julia";

	private static final String NAME_PETER = "Peter";

	private static final String NAME_ALINA = "Alina";

	private static final String NAME_THOMAS = "Thomas";

	private static final String NAME_BRIGITTE = "Brigitte";

	private static final String NAME_JANOSCH = "Janosch";

	private static final String NAME_JANINA = "Janina";

	private static final String NAME_LENNY = "Lenny";

	private static final String NAME_KARL = "Karl";

	private static final String NAME_HORST_A = "horsta";

	private static final String NAME_HORST_B = "horstb";

	private static final String NAME_SYSTEM = "system";

	private static final String TENANT_A = "tenant_a";

	private static final String TENANT_B = "tenant_b";

	private static final String TENANT_SYSTEM = "system";

	private final PatientRepository patientRepository;

	/**
	 * This method is executed upon startup, when the application context is loaded.
	 */
	@Override
	public void run(String... args) {
		/*
		 * In the demos, the different repositories are potentially secured by policy
		 * enforcement points (PEPs). Thus, back-end services like this should identify
		 * themselves as such, that the policy decision point (PDP) can make the correct
		 * decision and grant access to write this data.
		 *
		 * In this case, the process indicates that it is the user 'system' with the
		 * authority 'ROLE_SYSTEM'.
		 */

		var            authorities = AuthorityUtils.createAuthorityList("ROLE_SYSTEM");
		var            systemUser  = new TenantAwareUserDetails(TENANT_SYSTEM, NAME_SYSTEM, "", authorities);
		Authentication auth        = new UsernamePasswordAuthenticationToken(systemUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		// Create patients, if none are present
		// (else assume they are in persistent storage and do nothing)
		if (patientRepository.findAll().isEmpty()) {
			patientRepository.save(new Patient(null, "123456", NAME_LENNY, "DA63.Z/ME24.90",
					"Duodenal ulcer with acute haemorrhage.", NAME_JULIA, NAME_THOMAS, "+78(0)456-789", "A.3.47"));
			patientRepository.save(new Patient(null, "987654", NAME_KARL, "9B71.0Z/5A11", "Type 2 diabetes mellitus",
					NAME_ALINA, NAME_JANINA, "+78(0)456-567", "C.2.23"));
		}
	}

	/**
	 * This method is used by the demo applications to load user credentials into an
	 * in-memory UserDetailsManager.
	 * 
	 * @param inMem   an InMemoryUserDetailsManagerConfigurer
	 * @param encoder the selected password encoder
	 */
	public static void loadUsers(
			TenantAwareUserDetailsService inMem,
			PasswordEncoder encoder) {

		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_DOMINIC, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_VISITOR)));
		inMem.load(new TenantAwareUserDetails(TENANT_B, NAME_JULIA, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_DOCTOR)));
		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_PETER, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_DOCTOR)));
		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_ALINA, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_DOCTOR)));
		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_THOMAS, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_NURSE)));
		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_BRIGITTE, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_NURSE)));
		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_JANOSCH, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_NURSE)));
		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_JANINA, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_DOCTOR)));
		inMem.load(new TenantAwareUserDetails(TENANT_A, NAME_HORST_A, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_ADMIN)));
		inMem.load(new TenantAwareUserDetails(TENANT_B, NAME_HORST_B, encoder.encode(DEFAULT_RAW_PASSWORD),
				AuthorityUtils.createAuthorityList(ROLE_ADMIN)));

	}

}
