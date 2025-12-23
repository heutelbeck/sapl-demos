/*
 * Copyright (C) 2017-2025 Dominic Heutelbeck (dominic@heutelbeck.com)
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.sapl.r2dbc.data.DemoData;
import io.sapl.r2dbc.domain.Book;
import io.sapl.r2dbc.domain.LibraryUser;

/**
 * End-to-end integration tests for BookController at the HTTP level.
 * <p>
 * These tests verify the complete request flow including:
 * <ul>
 *   <li>HTTP routing and content negotiation</li>
 *   <li>Spring Security authentication</li>
 *   <li>SAPL policy enforcement with query rewriting</li>
 *   <li>JSON serialization of responses</li>
 * </ul>
 * <p>
 * Unlike {@link BookControllerIT}, these tests exercise the full WebFlux stack.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@DisplayName("BookController HTTP-level security")
class BookControllerWebIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
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

    private static LibraryUser testUser(String name, List<Integer> dataScope) {
        return new LibraryUser(name, 0, dataScope, List.of());
    }

    /**
     * Creates an authentication token with LibraryUser as principal.
     * This ensures SAPL policies can access subject.principal.dataScope.
     */
    private static UsernamePasswordAuthenticationToken authenticationFor(LibraryUser user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.authorities());
    }

    private static List<Book> booksInCategories(Set<Integer> categories) {
        return DemoData.DEMO_BOOKS.stream()
                .filter(book -> categories.contains(book.getCategory()))
                .toList();
    }

    @Nested
    @DisplayName("GET /")
    class FindAllEndpoint {

        static Stream<Arguments> authorizedUsers() {
            return Stream.of(
                Arguments.of("full-access",   List.of(1, 2, 3, 4, 5), Set.of(1, 2, 3, 4, 5), 15),
                Arguments.of("partial-access", List.of(1, 2),         Set.of(1, 2),          6),
                Arguments.of("single-section", List.of(5),            Set.of(5),             3)
            );
        }

        @ParameterizedTest(name = "{0}: expects {3} books from categories {2}")
        @MethodSource("authorizedUsers")
        void authorizedUser_receivesFilteredBooks(String userName, List<Integer> scope,
                                                   Set<Integer> expectedCategories, int expectedCount) {
            var user          = testUser(userName, scope);
            var expectedBooks = booksInCategories(expectedCategories);

            webTestClient
                .mutateWith(mockAuthentication(authenticationFor(user)))
                .get()
                .uri("/")
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
        @DisplayName("user with empty scope receives 403 Forbidden")
        void emptyScope_returnsForbidden() {
            var user = testUser("no-access", List.of());

            webTestClient
                .mutateWith(mockAuthentication(authenticationFor(user)))
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("unauthenticated request redirects to login")
        void unauthenticated_redirectsToLogin() {
            // Default Spring Security form login redirects unauthenticated requests
            webTestClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection();
        }
    }
}
