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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.demo.books.data.DemoData;
import io.sapl.demo.books.domain.Book;
import io.sapl.demo.books.domain.LibraryUser;

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
 * Unlike {@link BookControllerTests}, these tests exercise the full Spring MVC stack.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookController HTTP-level security")
class BookControllerWebTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Creates a test user (clean principal) with the specified data scope.
     */
    private static LibraryUser testUser(String name, List<Integer> dataScope) {
        return new LibraryUser(name, 0, dataScope, List.of());
    }

    /**
     * Creates an Authentication token with the LibraryUser as principal.
     */
    private static Authentication authFor(LibraryUser user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.authorities());
    }

    private static List<Book> booksInCategories(Set<Integer> categories) {
        return DemoData.DEMO_BOOKS.stream()
                .filter(book -> categories.contains(book.getCategory()))
                .toList();
    }

    @Nested
    class FindAllEndpoint {

        static Stream<Arguments> authorizedUsers() {
            return Stream.of(
                Arguments.of("full-access",    List.of(1, 2, 3, 4, 5), Set.of(1, 2, 3, 4, 5), 15),
                Arguments.of("partial-access", List.of(1, 2),          Set.of(1, 2),          6),
                Arguments.of("single-section", List.of(5),         Set.of(5),             3)
            );
        }

        @ParameterizedTest(name = "{0}: expects {3} books from categories {2}")
        @MethodSource("authorizedUsers")
        void authorizedUser_receivesFilteredBooks(String userName, List<Integer> scope,
                                                   Set<Integer> expectedCategories, int expectedCount) throws Exception {
            var user          = testUser(userName, scope);
            var expectedBooks = booksInCategories(expectedCategories);

            var result = mockMvc.perform(get("/").with(authentication(authFor(user))))
                    .andExpect(status().isOk())
                    .andReturn();

            var responseBody = result.getResponse().getContentAsString();
            var books = objectMapper.readValue(responseBody, new TypeReference<List<Book>>() {});

            assertThat(books)
                .hasSize(expectedCount)
                .containsExactlyInAnyOrderElementsOf(expectedBooks)
                .allMatch(book -> expectedCategories.contains(book.getCategory()));
        }

        @Test
        @DisplayName("user with empty scope receives 403 Forbidden")
        void emptyScope_returnsForbidden() throws Exception {
            var user = testUser("no-access", List.of());

            mockMvc.perform(get("/").with(authentication(authFor(user))))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("unauthenticated request redirects to login")
        void unauthenticated_redirectsToLogin() throws Exception {
            // Form login redirects unauthenticated requests to the login page
            mockMvc.perform(get("/"))
                    .andExpect(status().is3xxRedirection());
        }
    }
}
