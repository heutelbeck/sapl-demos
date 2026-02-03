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
package io.sapl.demo.books.rest;

import io.sapl.demo.books.data.DemoData;
import io.sapl.demo.books.domain.Book;
import io.sapl.demo.books.domain.LibraryUser;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
 * For full HTTP-level testing, see {@link BookControllerWebTests}.
 */
@SpringBootTest
@DisplayName("BookController method-level security")
class BookControllerTests {

    @Autowired
    BookController controller;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Creates a test user (clean principal) with the specified data scope.
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
     */
    private List<Book> findAllAsUser(LibraryUser user) {
        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.authorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return controller.findAll();
    }

    @Nested
    @DisplayName("when user has valid data scope")
    class PermitScenarios {

        static Stream<Arguments> usersWithExpectedCategories() {
            return Stream.of(
                Arguments.of("all-sections",      List.of(1, 2, 3, 4, 5), Set.of(1, 2, 3, 4, 5)),
                Arguments.of("children-and-scifi", List.of(1, 2),        Set.of(1, 2)),
                Arguments.of("science-and-mystery", List.of(3, 4),       Set.of(3, 4)),
                Arguments.of("classics-only",     List.of(5),            Set.of(5)),
                Arguments.of("single-category",   List.of(3),            Set.of(3))
            );
        }

        @ParameterizedTest(name = "user with scope {1} sees only books in categories {2}")
        @MethodSource("usersWithExpectedCategories")
        void returnsOnlyBooksMatchingUserScope(String userName, List<Integer> scope, Set<Integer> expectedCategories) {
            var user          = testUser(userName, scope);
            var expectedBooks = booksInCategories(expectedCategories);

            var actualBooks = findAllAsUser(user);

            assertThat(actualBooks)
                .as("User '%s' with scope %s should see exactly %d books in categories %s",
                    userName, scope, expectedBooks.size(), expectedCategories)
                .hasSize(expectedBooks.size())
                .containsExactlyInAnyOrderElementsOf(expectedBooks)
                .allMatch(book -> expectedCategories.contains(book.getCategory()));
        }
    }

    @Nested
    @DisplayName("when user has empty data scope")
    class DenyScenarios {

        @Test
        @DisplayName("user with empty scope is denied access")
        void emptyScope_accessDenied() {
            var user = testUser("no-access-user", List.of());

            assertThatThrownBy(() -> findAllAsUser(user))
                .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("user with null scope is denied access")
        void nullScope_accessDenied() {
            var user = testUser("null-scope-user", null);

            assertThatThrownBy(() -> findAllAsUser(user))
                .isInstanceOf(AccessDeniedException.class);
        }
    }
}
