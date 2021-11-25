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
package org.demo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * This class is for loading a demo data set into the database. The run method is executed
 * after the application context is loaded.
 */
@Component
@RequiredArgsConstructor
public class DemoData implements CommandLineRunner {

	// Demo Data Strings

	private static final String ROLE_DOCTOR = "DOCTOR";

	private static final String ROLE_NURSE = "NURSE";

	private static final String ROLE_VISITOR = "VISITOR";

	private static final String ROLE_ADMIN = "ADMIN";

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

	private static final String NAME_HORST = "Horst";

	private static final String DEFAULT_RAW_PASSWORD = "password";

	private final PatientRepository patientRepository;

	private final RelationRepository relationRepository;

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
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_SYSTEM"));
		Authentication auth = new UsernamePasswordAuthenticationToken("system", null, authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		// Create patients, if none are present
		// (else assume they are in persistent storage and do nothing)
		if (patientRepository.findAll().size() == 0) {
			patientRepository.save(new Patient(null, "123456", NAME_LENNY, "DA63.Z/ME24.90",
					"Duodenal ulcer with acute haemorrhage.", NAME_JULIA, NAME_THOMAS, "+78(0)456-789", "A.3.47"));
			patientRepository.save(new Patient(null, "987654", NAME_KARL, "9B71.0Z/5A11", "Type 2 diabetes mellitus",
					NAME_ALINA, NAME_JANINA, "+78(0)456-567", "C.2.23"));
		}
		// Establish relations between users and patients, if none are present
		// (else assume they are in persistent storage and do nothing)
		if (StreamSupport.stream(relationRepository.findAll().spliterator(), false).findAny().isEmpty()) {
			relationRepository.save(new Relation(NAME_DOMINIC, patientRepository.findByName(NAME_LENNY).get().getId()));
			relationRepository.save(new Relation(NAME_JULIA, patientRepository.findByName(NAME_KARL).get().getId()));
			relationRepository.save(new Relation(NAME_ALINA, patientRepository.findByName(NAME_KARL).get().getId()));
			relationRepository.save(new Relation(NAME_JANOSCH, patientRepository.findByName(NAME_KARL).get().getId()));
		}
	}

	/**
	 * This method is used by the demo applications to load user credentials into an
	 * in-memory UserDetailsManager.
	 * @param inMem an InMemoryUserDetailsManagerConfigurer
	 * @param encoder the selected password encoder
	 */
	public static void loadUsers(InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMem,
			PasswordEncoder encoder) {
		inMem.withUser(NAME_DOMINIC).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_VISITOR);
		inMem.withUser(NAME_JULIA).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_DOCTOR);
		inMem.withUser(NAME_PETER).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_DOCTOR);
		inMem.withUser(NAME_ALINA).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_DOCTOR);
		inMem.withUser(NAME_THOMAS).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_NURSE);
		inMem.withUser(NAME_BRIGITTE).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_NURSE);
		inMem.withUser(NAME_JANOSCH).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_NURSE);
		inMem.withUser(NAME_JANINA).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_NURSE);
		inMem.withUser(NAME_HORST).password(encoder.encode(DEFAULT_RAW_PASSWORD)).roles(ROLE_ADMIN);
	}

}
