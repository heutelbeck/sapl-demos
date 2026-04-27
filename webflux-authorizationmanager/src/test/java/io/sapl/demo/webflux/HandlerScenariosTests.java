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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.sapl.api.pdp.Decision;
import io.sapl.demo.webflux.handlers.AuditProbe;

/**
 * End-to-end tests for the policy-driven HTTP signal handlers wired into the
 * reactive demo through the {@code SaplServerHttpSecurityConfigurer}.
 * Covers:
 * <ul>
 * <li>Audit advice firing on every decision (DecisionSignal).</li>
 * <li>Request-mutation obligation injecting a header the controller
 * observes (HttpRequestMutationSignal).</li>
 * <li>Response obligation stamping a header on the outbound response
 * (HttpResponseSignal).</li>
 * <li>Authenticated denial obligation shaping the deny response into a
 * custom HTTP 418 page (HttpDenialSignal).</li>
 * <li>Anonymous denial routing through Spring Security's authentication
 * entry point unchanged (no SAPL deny handler involvement).</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("HTTP signal handlers wired through the SAPL reactive configurer")
class HandlerScenariosTests {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    AuditProbe auditProbe;

    @BeforeEach
    void resetProbe() {
        auditProbe.reset();
    }

    @Nested
    @DisplayName("Audit advice on DecisionSignal")
    class AuditAdvice {

        @Test
        @DisplayName("permit on /public records one audit entry with PERMIT")
        @WithAnonymousUser
        void anonymousPublicRecordsPermit() {
            webTestClient.get().uri("/public").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isOk();
            assertThat(auditProbe.entries()).isNotEmpty()
                    .allMatch(entry -> entry.decision() == Decision.PERMIT);
        }

        @Test
        @DisplayName("deny on /teapot for an authenticated user records a DENY entry")
        @WithMockUser(username = "user", roles = "USER")
        void authenticatedTeapotRecordsDeny() {
            webTestClient.get().uri("/teapot").accept(MediaType.TEXT_PLAIN).exchange().expectStatus()
                    .isEqualTo(HttpStatus.I_AM_A_TEAPOT);
            assertThat(auditProbe.entries()).anyMatch(entry -> entry.decision() == Decision.DENY);
        }
    }

    @Nested
    @DisplayName("Request mutation on HttpRequestMutationSignal")
    class RequestMutation {

        @Test
        @DisplayName("policy-injected X-Correlation-Id header is visible to the controller")
        @WithMockUser(username = "user", roles = "USER")
        void controllerSeesInjectedHeader() {
            webTestClient.get().uri("/echo-correlation").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isOk()
                    .expectBody(String.class).isEqualTo("demo-correlation-id");
        }
    }

    @Nested
    @DisplayName("Response shaping on HttpResponseSignal")
    class ResponseShaping {

        @Test
        @DisplayName("permit on /secret stamps the X-Authorized-By header on the outbound response")
        @WithMockUser(username = "user", roles = "USER")
        void secretCarriesAuthorizedByHeader() {
            webTestClient.get().uri("/secret").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isOk()
                    .expectHeader().valueEquals("X-Authorized-By", "SAPL").expectBody(String.class)
                    .isEqualTo("Secret information");
        }
    }

    @Nested
    @DisplayName("Denial customisation on HttpDenialSignal")
    class DenialCustomisation {

        @Test
        @DisplayName("authenticated deny on /teapot produces 418 with the custom body")
        @WithMockUser(username = "user", roles = "USER")
        void teapotProducesCustomDenyPage() {
            webTestClient.get().uri("/teapot").accept(MediaType.TEXT_PLAIN).exchange().expectStatus()
                    .isEqualTo(HttpStatus.I_AM_A_TEAPOT).expectBody(String.class)
                    .isEqualTo("I'm a teapot. Brew tea instead.");
        }

        @Test
        @DisplayName("anonymous deny on /secret routes to Spring's entry point, not the SAPL deny handler")
        @WithAnonymousUser
        void anonymousSecretGoesToEntryPoint() {
            webTestClient.get().uri("/secret").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isUnauthorized();
        }
    }
}
