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
package io.sapl.mvc.demo.domain;

import io.sapl.mvc.demo.MvcDemoApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext
@SpringBootTest(classes = MvcDemoApplication.class)
@DisplayName("PatientRepository method-level security")
class PatientRepositoryIT {

    @Autowired
    PatientRepository patientRepository;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    void setAuthentication(String username, String role) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        var user = User.builder()
                .username(username)
                .password("password")
                .authorities(authorities)
                .build();
        var auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    @DisplayName("findAll - patient list access")
    class FindAllTests {

        static Stream<Arguments> authenticatedUsers() {
            return Stream.of(
                Arguments.of("Julia", "DOCTOR"),
                Arguments.of("Thomas", "NURSE"),
                Arguments.of("Dominic", "VISITOR"),
                Arguments.of("Horst", "ADMIN")
            );
        }

        @ParameterizedTest(name = "{0} ({1}) can see patient list")
        @MethodSource("authenticatedUsers")
        void authenticatedUserCanSeePatientList(String username, String role) {
            setAuthentication(username, role);
            List<Patient> patients = patientRepository.findAll();
            assertThat(patients).isNotEmpty().hasSize(2);
        }
    }

    @Nested
    @DisplayName("findById - patient record access")
    class FindByIdTests {

        @Test
        @DisplayName("Doctor can access patient record with full data")
        void doctorCanAccessPatientWithFullData() {
            setAuthentication("Julia", "DOCTOR");
            var patient = patientRepository.findById(1L);
            assertThat(patient).isPresent();
            assertThat(patient.get().getName()).isEqualTo("Lenny");
            assertThat(patient.get().getDiagnosisText()).isNotNull();
        }

        @Test
        @DisplayName("Nurse can access patient record with full data")
        void nurseCanAccessPatientWithFullData() {
            setAuthentication("Thomas", "NURSE");
            var patient = patientRepository.findById(1L);
            assertThat(patient).isPresent();
            assertThat(patient.get().getName()).isEqualTo("Lenny");
        }

        @Test
        @DisplayName("Admin can access patient")
        void adminCanAccessPatient() {
            setAuthentication("Horst", "ADMIN");
            var patient = patientRepository.findById(1L);
            assertThat(patient).isPresent();
            assertThat(patient.get().getName()).isEqualTo("Lenny");
        }

        @Test
        @DisplayName("Visitor can access patient")
        void visitorCanAccessPatient() {
            setAuthentication("Dominic", "VISITOR");
            var patient = patientRepository.findById(1L);
            assertThat(patient).isPresent();
        }
    }

    @Nested
    @DisplayName("save - patient creation")
    class SaveTests {

        @Test
        @DisplayName("Doctor can create new patient")
        void doctorCanCreatePatient() {
            setAuthentication("Julia", "DOCTOR");
            Patient newPatient = new Patient();
            newPatient.setName("TestPatient");
            newPatient.setMedicalRecordNumber("999999");
            newPatient.setAttendingDoctor("Julia");
            newPatient.setAttendingNurse("Thomas");
            Patient saved = patientRepository.save(newPatient);
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("TestPatient");
        }

        @Test
        @DisplayName("Nurse cannot create new patient")
        void nurseCannotCreatePatient() {
            setAuthentication("Thomas", "NURSE");
            Patient newPatient = new Patient();
            newPatient.setName("TestPatient");
            assertThatThrownBy(() -> patientRepository.save(newPatient))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("Visitor cannot create new patient")
        void visitorCannotCreatePatient() {
            setAuthentication("Dominic", "VISITOR");
            Patient newPatient = new Patient();
            newPatient.setName("TestPatient");
            assertThatThrownBy(() -> patientRepository.save(newPatient))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("update methods - field updates")
    class UpdateTests {

        @Test
        @DisplayName("Doctor can update attending nurse")
        void doctorCanUpdateAttendingNurse() {
            setAuthentication("Julia", "DOCTOR");
            patientRepository.updateAttendingNurseById("Brigitte", 1L);
            var patient = patientRepository.findById(1L);
            assertThat(patient).isPresent();
            assertThat(patient.get().getAttendingNurse()).isEqualTo("Brigitte");
        }

        @Test
        @DisplayName("Nurse can update phone number")
        void nurseCanUpdatePhoneNumber() {
            setAuthentication("Thomas", "NURSE");
            patientRepository.updatePhoneNumberById("+49(0)123-456", 1L);
            var patient = patientRepository.findById(1L);
            assertThat(patient).isPresent();
            assertThat(patient.get().getPhoneNumber()).isEqualTo("+49(0)123-456");
        }

        @Test
        @DisplayName("Nurse can update room number")
        void nurseCanUpdateRoomNumber() {
            setAuthentication("Thomas", "NURSE");
            patientRepository.updateRoomNumberById("B.1.12", 1L);
            var patient = patientRepository.findById(1L);
            assertThat(patient).isPresent();
            assertThat(patient.get().getRoomNumber()).isEqualTo("B.1.12");
        }
    }
}
