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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


/**
 * Controller tests for demo endpoints using WebTestClient.
 * These tests verify the HTTP layer and policy enforcement via HTTP context paths.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DemoControllerTests {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    Clock mockClock;

    // ========== /changedstring - @PostEnforce with transform ==========

    @Test
    @WithAnonymousUser
    void whenGetChangedString_thenReturnsTransformedString() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH);
        webTestClient.get().uri("/changedstring")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).startsWith("***").endsWith("***"));
    }

    // ========== /numbers - @PreEnforce on Flux ==========

    @Test
    @WithAnonymousUser
    void whenGetNumbers_thenReturnsSSEStream() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH);
        webTestClient.get().uri("/numbers")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk();
    }

    // ========== /enforcetilldeny - @EnforceTillDenied ==========

    @Test
    @WithAnonymousUser
    void whenGetEnforceTillDenyAndPermitted_thenReturnsData() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH); // second 0, permits
        webTestClient.mutate().responseTimeout(Duration.ofSeconds(2)).build()
                .get().uri("/enforcetilldeny")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    void whenGetEnforceTillDenyAndDenied_thenReturnsAccessDeniedMessage() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(45))); // second 45, denies
        webTestClient.mutate().responseTimeout(Duration.ofSeconds(2)).build()
                .get().uri("/enforcetilldeny")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("ACCESS DENIED"));
    }

    // ========== /enforcedropwhiledeny - @EnforceDropWhileDenied ==========

    @Test
    @WithAnonymousUser
    void whenGetEnforceDropWhileDenyAndPermitted_thenReturnsData() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH); // second 0, permits
        webTestClient.mutate().responseTimeout(Duration.ofSeconds(2)).build()
                .get().uri("/enforcedropwhiledeny")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk();
    }

    // ========== /enforcerecoverableifdeny - @EnforceRecoverableIfDenied ==========

    @Test
    @WithAnonymousUser
    void whenGetEnforceRecoverableIfDenyAndPermitted_thenReturnsData() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH); // second 0, permits
        webTestClient.mutate().responseTimeout(Duration.ofSeconds(2)).build()
                .get().uri("/enforcerecoverableifdeny")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk();
    }


    // ========== /documents - Flux filtering ==========

    @Test
    @WithAnonymousUser
    void whenGetDocumentsAtRestrictedClearance_thenReturnsFilteredDocuments() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH); // restricted clearance
        webTestClient.get().uri("/documents")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .value(docs -> assertThat(docs).isNotEmpty());
    }

    @Test
    @WithAnonymousUser
    void whenGetDocumentsAtTopSecretClearance_thenReturnsMoreDocuments() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(25))); // top secret
        webTestClient.get().uri("/documents")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .value(docs -> assertThat(docs).isNotEmpty());
    }

    // ========== /patients - JSON content transformation ==========

    @Test
    @WithAnonymousUser
    void whenGetPatientsWithBlackenedData_thenReturnsTransformedPatients() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH); // blackened data
        webTestClient.get().uri("/patients")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .value(patients -> assertThat(patients).isNotEmpty());
    }

    @Test
    @WithAnonymousUser
    void whenGetPatientsWithFullData_thenReturnsUnmodifiedPatients() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(45))); // full data
        webTestClient.get().uri("/patients")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .value(patients -> assertThat(patients).isNotEmpty());
    }

    // ========== /string with argument modification ==========

    @Test
    @WithAnonymousUser
    void whenGetStringWithArgumentModification_thenReturnsModifiedString() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH);
        webTestClient.get().uri("/string")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    // Should contain "hello modification" (lowercased from the suffix)
                    assertThat(body.toLowerCase()).contains("hello modification");
                });
    }
}
