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
package io.sapl.mvc.demo.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import io.sapl.spring.method.metadata.PostEnforce;
import io.sapl.spring.method.metadata.PreEnforce;
import jakarta.transaction.Transactional;

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

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Patient p set p.name = ?1 where p.id = ?2")
	void updateNameById(String name, Long id);

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Patient p set p.diagnosisText = ?1 where p.id = ?2")
	void updateDiagnosisTextById(String diagnosisText, Long id);

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Patient p set p.icd11Code = ?1 where p.id = ?2")
	void updateIcd11CodeById(String icd11Code, Long id);

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Patient p set p.phoneNumber = ?1 where p.id = ?2")
	void updatePhoneNumberById(String phoneNumber, Long id);

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Patient p set p.attendingDoctor = ?1 where p.id = ?2")
	void updateAttendingDoctorById(String attendingDoctor, Long id);

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Patient p set p.attendingNurse = ?1 where p.id = ?2")
	void updateAttendingNurseById(String attendingNurse, Long id);

	@Modifying
	@PreEnforce
	@Transactional
	@Query("update Patient p set p.roomNumber = ?1 where p.id = ?2")
	void updateRoomNumberById(String roomNumber, Long id);

}
