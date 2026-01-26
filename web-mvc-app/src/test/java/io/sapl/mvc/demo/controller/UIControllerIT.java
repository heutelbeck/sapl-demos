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
package io.sapl.mvc.demo.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import io.sapl.mvc.demo.MvcDemoApplication;

@DirtiesContext
@AutoConfigureMockMvc
@SpringBootTest(classes = MvcDemoApplication.class)
@DisplayName("UIController HTTP-level security")
class UIControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("GET /patients - patient list")
    class GetPatientsTests {

        static Stream<Arguments> authenticatedUsers() {
            return Stream.of(
                Arguments.of("Julia", "DOCTOR"),
                Arguments.of("Thomas", "NURSE"),
                Arguments.of("Dominic", "VISITOR"),
                Arguments.of("Horst", "ADMIN")
            );
        }

        @ParameterizedTest(name = "{0} ({1}) can access patient list")
        @MethodSource("authenticatedUsers")
        void authenticatedUserCanAccessPatientList(String username, String role) throws Exception {
            mockMvc.perform(get("/patients").with(user(username).roles(role)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("patients"))
                    .andExpect(model().attributeExists("patients"));
        }

        @Test
        @DisplayName("Unauthenticated user is redirected to login")
        void unauthenticatedUserRedirectedToLogin() throws Exception {
            mockMvc.perform(get("/patients").with(anonymous()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /patients/{id} - patient detail")
    class GetPatientTests {

        @Test
        @DisplayName("Doctor can access patient detail")
        void doctorCanAccessPatientDetail() throws Exception {
            mockMvc.perform(get("/patients/1").with(user("Julia").roles("DOCTOR")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("patient"))
                    .andExpect(model().attributeExists("patient"));
        }
    }

    @Nested
    @DisplayName("GET /patients/new - new patient form")
    class NewPatientTests {

        @Test
        @DisplayName("Doctor can access new patient form")
        void doctorCanAccessNewPatientForm() throws Exception {
            mockMvc.perform(get("/patients/new").with(user("Julia").roles("DOCTOR")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("newPatient"));
        }

        @Test
        @DisplayName("Nurse cannot access new patient form")
        void nurseCannotAccessNewPatientForm() throws Exception {
            mockMvc.perform(get("/patients/new").with(user("Thomas").roles("NURSE")))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /patients/{id} - delete patient")
    class DeletePatientTests {

        @Test
        @DisplayName("Doctor can access delete endpoint")
        void doctorCanAccessDeleteEndpoint() throws Exception {
            mockMvc.perform(delete("/patients/1").with(user("Julia").roles("DOCTOR")).with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Nurse cannot access delete endpoint")
        void nurseCannotAccessDeleteEndpoint() throws Exception {
            mockMvc.perform(delete("/patients/1").with(user("Thomas").roles("NURSE")).with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /patients/{id}/update - update patient form")
    class UpdatePatientFormTests {

        @Test
        @DisplayName("Doctor can access update form")
        void doctorCanAccessUpdateForm() throws Exception {
            mockMvc.perform(get("/patients/1/update").with(user("Julia").roles("DOCTOR")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("updatePatient"));
        }

        @Test
        @DisplayName("Visitor cannot access update form")
        void visitorCannotAccessUpdateForm() throws Exception {
            mockMvc.perform(get("/patients/1/update").with(user("Dominic").roles("VISITOR")))
                    .andExpect(status().isForbidden());
        }
    }
}
