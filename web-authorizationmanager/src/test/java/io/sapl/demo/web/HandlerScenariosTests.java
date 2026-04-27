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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import io.sapl.api.pdp.Decision;
import io.sapl.demo.web.handlers.AuditProbe;

/**
 * End-to-end tests for the policy-driven HTTP signal handlers wired into the
 * demo through the {@code SaplHttpSecurityConfigurer}. Covers:
 * <ul>
 * <li>Audit advice firing on every decision (DecisionSignal).</li>
 * <li>Request-mutation obligation injecting a header the controller observes
 * (HttpRequestMutationSignal).</li>
 * <li>Response obligation stamping a header on the outbound response
 * (HttpResponseSignal).</li>
 * <li>Authenticated denial obligation shaping the deny response into a
 * custom HTTP 418 page (HttpDenialSignal).</li>
 * <li>Anonymous denial routing through Spring Security's authentication
 * entry point unchanged (no SAPL deny handler involvement).</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("HTTP signal handlers wired through the SAPL configurer")
class HandlerScenariosTests {

    @Autowired
    MockMvc mockMvc;

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
        void anonymousPublicRecordsPermit() throws Exception {
            mockMvc.perform(get("/public")).andExpect(status().isOk());
            assertThat(auditProbe.entries()).isNotEmpty()
                    .allMatch(entry -> entry.decision() == Decision.PERMIT);
        }

        @Test
        @DisplayName("deny on /teapot for an authenticated user records a DENY entry")
        void authenticatedTeapotRecordsDeny() throws Exception {
            mockMvc.perform(get("/teapot").with(user("user").roles("USER"))).andExpect(status().is(418));
            assertThat(auditProbe.entries()).anyMatch(entry -> entry.decision() == Decision.DENY);
        }
    }

    @Nested
    @DisplayName("Request mutation on HttpRequestMutationSignal")
    class RequestMutation {

        @Test
        @DisplayName("policy-injected X-Correlation-Id header is visible to the controller")
        void controllerSeesInjectedHeader() throws Exception {
            mockMvc.perform(get("/echo-correlation").with(user("user").roles("USER"))).andExpect(status().isOk())
                    .andExpect(content().string("demo-correlation-id"));
        }
    }

    @Nested
    @DisplayName("Response shaping on HttpResponseSignal")
    class ResponseShaping {

        @Test
        @DisplayName("permit on /secret stamps the X-Authorized-By header on the outbound response")
        void secretCarriesAuthorizedByHeader() throws Exception {
            mockMvc.perform(get("/secret").with(user("user").roles("USER"))).andExpect(status().isOk())
                    .andExpect(header().string("X-Authorized-By", "SAPL"))
                    .andExpect(content().string("secret information"));
        }
    }

    @Nested
    @DisplayName("Denial customisation on HttpDenialSignal")
    class DenialCustomisation {

        @Test
        @DisplayName("authenticated deny on /teapot produces 418 with the custom body")
        void teapotProducesCustomDenyPage() throws Exception {
            mockMvc.perform(get("/teapot").with(user("user").roles("USER"))).andExpect(status().is(418))
                    .andExpect(content().string("I'm a teapot. Brew tea instead."));
        }

        @Test
        @DisplayName("anonymous deny on /secret routes to Spring's entry point, not the SAPL deny handler")
        @WithAnonymousUser
        void anonymousSecretGoesToEntryPoint() throws Exception {
            mockMvc.perform(get("/secret").accept(MediaType.TEXT_PLAIN)).andExpect(status().is3xxRedirection());
        }
    }
}
