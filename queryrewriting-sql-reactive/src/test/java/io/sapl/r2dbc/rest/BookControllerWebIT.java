/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.r2dbc.rest;

import io.sapl.r2dbc.data.DemoData;
import io.sapl.r2dbc.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration tests for BookController at the HTTP level.
 * <p>
 * These tests verify the complete request flow including:
 * <ul>
 *   <li>HTTP routing and content negotiation</li>
 *   <li>Spring Security authentication via HTTP Basic</li>
 *   <li>SAPL policy enforcement with query rewriting</li>
 *   <li>JSON serialization of responses</li>
 * </ul>
 * <p>
 * Uses real HTTP with actual demo users (boss, zoe, bob, ann, pat) to verify
 * the demo application works as a user would experience it.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "30000")
@Testcontainers
@DisplayName("BookController HTTP-level security")
class BookControllerWebIT {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17")
            .withInitScript("init_scripts/schema.sql");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s",
                postgres.getHost(), postgres.getFirstMappedPort(), postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Autowired
    WebTestClient webTestClient;

    private static List<Book> booksInCategories(Set<Integer> categories) {
        return DemoData.DEMO_BOOKS.stream()
                .filter(book -> categories.contains(book.getCategory()))
                .toList();
    }

    @Nested
    @DisplayName("GET /")
    class FindAllEndpoint {

        /**
         * Test data using real demo users from DemoData:
         *   boss - all sections (1,2,3,4,5) -> 15 books
         *   zoe  - sections 1,2 -> 6 books
         *   bob  - sections 3,4 -> 6 books
         *   ann  - section 5 -> 3 books
         */
        static Stream<Arguments> authorizedUsers() {
            return Stream.of(
                Arguments.of("boss", Set.of(1, 2, 3, 4, 5), 15),
                Arguments.of("zoe",  Set.of(1, 2),          6),
                Arguments.of("bob",  Set.of(3, 4),          6),
                Arguments.of("ann",  Set.of(5),             3)
            );
        }

        @ParameterizedTest(name = "{0}: expects {2} books from categories {1}")
        @MethodSource("authorizedUsers")
        void authorizedUser_receivesFilteredBooks(String username, Set<Integer> expectedCategories, int expectedCount) {
            var expectedBooks = booksInCategories(expectedCategories);

            webTestClient
                .get()
                .uri("/")
                .headers(headers -> headers.setBasicAuth(username, DemoData.DEFAULT_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book.class)
                .hasSize(expectedCount)
                .value(books -> {
                    assertThat(books)
                        .containsExactlyInAnyOrderElementsOf(expectedBooks);
                    assertThat(books)
                        .allMatch(book -> expectedCategories.contains(book.getCategory()));
                });
        }

        @Test
        @DisplayName("pat (no sections) receives 403 Forbidden")
        void emptyScope_returnsForbidden() {
            webTestClient
                .get()
                .uri("/")
                .headers(headers -> headers.setBasicAuth("pat", DemoData.DEFAULT_PASSWORD))
                .exchange()
                .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("unauthenticated request receives 401 Unauthorized")
        void unauthenticated_returnsUnauthorized() {
            webTestClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isUnauthorized();
        }
    }
}
