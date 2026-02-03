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
package io.sapl.demo.webflux;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the demo endpoints using WebTestClient.
 * These tests verify the HTTP layer and SAPL policy enforcement
 * via the filter chain authorization manager.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("DemoController HTTP-level security")
class DemoControllerTests {

    @Autowired
    WebTestClient webTestClient;

    @Nested
    @DisplayName("GET /public - public endpoint")
    class PublicEndpointTests {

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous user can access public endpoint")
        void whenAnonymousAccessesPublic_thenPermitted() {
            webTestClient.get().uri("/public")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).isEqualTo("Public information"));
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("USER can access public endpoint")
        void whenUserAccessesPublic_thenPermitted() {
            webTestClient.get().uri("/public")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).isEqualTo("Public information"));
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        @DisplayName("ADMIN can access public endpoint")
        void whenAdminAccessesPublic_thenPermitted() {
            webTestClient.get().uri("/public")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).isEqualTo("Public information"));
        }
    }

    @Nested
    @DisplayName("GET /secret - protected endpoint")
    class SecretEndpointTests {

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous user is unauthorized")
        void whenAnonymousAccessesSecret_thenUnauthorized() {
            webTestClient.get().uri("/secret")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("USER can access secret endpoint")
        void whenUserAccessesSecret_thenPermitted() {
            webTestClient.get().uri("/secret")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).isEqualTo("Secret information"));
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        @DisplayName("ADMIN can access secret endpoint")
        void whenAdminAccessesSecret_thenPermitted() {
            webTestClient.get().uri("/secret")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).isEqualTo("Secret information"));
        }
    }

    @Nested
    @DisplayName("Basic authentication")
    class BasicAuthTests {

        @Test
        @DisplayName("Valid credentials can access secret endpoint")
        void whenValidCredentialsAccessSecret_thenPermitted() {
            webTestClient.get().uri("/secret")
                    .headers(headers -> headers.setBasicAuth("user", "user"))
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).isEqualTo("Secret information"));
        }

        @Test
        @DisplayName("Invalid credentials are rejected")
        void whenInvalidCredentialsAccessSecret_thenUnauthorized() {
            webTestClient.get().uri("/secret")
                    .headers(headers -> headers.setBasicAuth("user", "wrongpassword"))
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("No credentials returns unauthorized")
        void whenNoCredentialsAccessSecret_thenUnauthorized() {
            webTestClient.get().uri("/secret")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("Valid credentials can access public endpoint")
        void whenValidCredentialsAccessPublic_thenPermitted() {
            webTestClient.get().uri("/public")
                    .headers(headers -> headers.setBasicAuth("user", "user"))
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).isEqualTo("Public information"));
        }
    }
}
