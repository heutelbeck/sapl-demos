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
package io.sapl.demo.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the demo endpoints using MockMvc.
 * These tests verify the HTTP layer and SAPL policy enforcement
 * via the filter chain authorization manager.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("RestService HTTP-level security")
class RestServiceTests {

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("GET /public - public endpoint")
    class PublicEndpointTests {

        static Stream<Arguments> allUsers() {
            return Stream.of(
                Arguments.of("user", "USER"),
                Arguments.of("admin", "ADMIN")
            );
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous user can access public endpoint")
        void whenAnonymousAccessesPublic_thenPermitted() throws Exception {
            mockMvc.perform(get("/public")
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("public information"));
        }

        @ParameterizedTest(name = "{0} ({1}) can access public endpoint")
        @MethodSource("allUsers")
        void authenticatedUserCanAccessPublic(String username, String role) throws Exception {
            mockMvc.perform(get("/public")
                    .with(user(username).roles(role))
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("public information"));
        }
    }

    @Nested
    @DisplayName("GET /secret - protected endpoint")
    class SecretEndpointTests {

        static Stream<Arguments> authenticatedUsers() {
            return Stream.of(
                Arguments.of("user", "USER"),
                Arguments.of("admin", "ADMIN")
            );
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous user is redirected to login")
        void whenAnonymousAccessesSecret_thenRedirected() throws Exception {
            mockMvc.perform(get("/secret")
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().is3xxRedirection());
        }

        @ParameterizedTest(name = "{0} ({1}) can access secret endpoint")
        @MethodSource("authenticatedUsers")
        void authenticatedUserCanAccessSecret(String username, String role) throws Exception {
            mockMvc.perform(get("/secret")
                    .with(user(username).roles(role))
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("secret information"));
        }
    }

    @Nested
    @DisplayName("Basic authentication")
    class BasicAuthTests {

        @Test
        @DisplayName("Valid credentials can access secret endpoint")
        void whenValidCredentialsAccessSecret_thenPermitted() throws Exception {
            mockMvc.perform(get("/secret")
                    .with(httpBasic("user", "user"))
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("secret information"));
        }

        @Test
        @DisplayName("Invalid credentials are rejected")
        void whenInvalidCredentialsAccessSecret_thenUnauthorized() throws Exception {
            mockMvc.perform(get("/secret")
                    .with(httpBasic("user", "wrongpassword"))
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("No credentials redirects to login")
        void whenNoCredentialsAccessSecret_thenRedirected() throws Exception {
            mockMvc.perform(get("/secret")
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Valid credentials can access public endpoint")
        void whenValidCredentialsAccessPublic_thenPermitted() throws Exception {
            mockMvc.perform(get("/public")
                    .with(httpBasic("user", "user"))
                    .accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andExpect(content().string("public information"));
        }
    }
}
