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

import java.util.List;
import java.util.Optional;

import io.sapl.spring.method.metadata.PostEnforce;
import io.sapl.spring.method.metadata.PreEnforce;

public interface PatientRepository {

	@PostEnforce(resource = "returnObject")
	Optional<Patient> findById(Long id);

	@PreEnforce
	Optional<Patient> findByName(String name);

	@PreEnforce
	List<Patient> findAll();

	@PreEnforce
	Patient save(Patient patient);

	@PreEnforce
	void deleteById(Long id);

}
