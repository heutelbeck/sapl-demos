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
package io.sapl.mongo.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.sapl.mongo.data.DemoData;
import io.sapl.mongo.domain.Book;
import io.sapl.mongo.domain.LibraryUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Integration tests for BookController method-level security.
 * <p>
 * These tests verify SAPL policy enforcement at the controller method level.
 * They test that:
 * <ul>
 *   <li>Users with valid scopes receive only books matching their allowed categories</li>
 *   <li>Users with empty scopes are denied access entirely</li>
 * </ul>
 * <p>
 * Note: These tests invoke controller methods directly, bypassing HTTP routing.
 * For full HTTP-level testing, see {@code BookControllerWebIT}.
 */
@SpringBootTest
@Testcontainers
@DisplayName("BookController method-level security")
class BookControllerIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @DynamicPropertySource
    static void configureMongoDb(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    BookController controller;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Creates a test user with the specified data scope.
     */
    private static LibraryUser testUser(String name, List<Integer> dataScope) {
        return new LibraryUser(name, 0, dataScope, List.of());
    }

    /**
     * Filters books by category membership.
     * This decouples test expectations from DemoData ordering.
     */
    private static List<Book> booksInCategories(Set<Integer> categories) {
        return DemoData.DEMO_BOOKS.stream()
                .filter(book -> categories.contains(book.getCategory()))
                .toList();
    }

    /**
     * Executes controller.findAll() with the given user's security context.
     * The user (LibraryUser) is set directly as the principal - no credentials needed.
     */
    private Mono<List<Book>> findAllAsUser(LibraryUser user) {
        var authentication  = new UsernamePasswordAuthenticationToken(user, null, user.authorities());
        var securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        return controller.findAll()
                .collectList()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    @Nested
    @DisplayName("when user has valid data scope")
    class PermitScenarios {

        static Stream<Arguments> usersWithExpectedCategories() {
            return Stream.of(
                Arguments.of("all-sections",     List.of(1, 2, 3, 4, 5), Set.of(1, 2, 3, 4, 5)),
                Arguments.of("children-and-scifi", List.of(1, 2),       Set.of(1, 2)),
                Arguments.of("science-and-mystery", List.of(3, 4),      Set.of(3, 4)),
                Arguments.of("classics-only",    List.of(5),            Set.of(5)),
                Arguments.of("single-category",  List.of(3),            Set.of(3))
            );
        }

        @ParameterizedTest(name = "user with scope {1} sees only books in categories {2}")
        @MethodSource("usersWithExpectedCategories")
        void returnsOnlyBooksMatchingUserScope(String userName, List<Integer> scope, Set<Integer> expectedCategories) {
            var user          = testUser(userName, scope);
            var expectedBooks = booksInCategories(expectedCategories);

            StepVerifier.create(findAllAsUser(user))
                    .assertNext(actualBooks -> {
                        assertThat(actualBooks)
                            .as("User '%s' with scope %s should see exactly %d books in categories %s",
                                userName, scope, expectedBooks.size(), expectedCategories)
                            .hasSize(expectedBooks.size())
                            .containsExactlyInAnyOrderElementsOf(expectedBooks);

                        // Verify no books from unauthorized categories leaked through
                        assertThat(actualBooks)
                            .as("No books from unauthorized categories should be present")
                            .allMatch(book -> expectedCategories.contains(book.getCategory()));
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("when user has empty data scope")
    class DenyScenarios {

        @Test
        @DisplayName("user with empty scope is denied access")
        void emptyScope_accessDenied() {
            var user = testUser("no-access-user", List.of());

            StepVerifier.create(findAllAsUser(user))
                    .expectErrorMatches(error ->
                        error.getClass().getSimpleName().contains("AccessDenied") ||
                        error.getMessage() != null && error.getMessage().contains("Access Denied"))
                    .verify();
        }

        @Test
        @DisplayName("user with null scope is denied access")
        void nullScope_accessDenied() {
            var user = testUser("null-scope-user", null);

            StepVerifier.create(findAllAsUser(user))
                    .expectErrorMatches(error ->
                        error.getClass().getSimpleName().contains("AccessDenied") ||
                        error.getMessage() != null && error.getMessage().contains("Access Denied"))
                    .verify();
        }
    }
}
