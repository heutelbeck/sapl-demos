/*
 * Copyright (C) 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.springdatamongoreactivedemo;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import io.sapl.springdatamongoreactivedemo.data.DemoData;
import io.sapl.springdatamongoreactivedemo.domain.Book;
import io.sapl.springdatamongoreactivedemo.domain.LibraryUser;
import io.sapl.springdatamongoreactivedemo.integration.TestContainerBase;
import io.sapl.springdatamongoreactivedemo.rest.BookController;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BookControllerTests extends TestContainerBase {

    @Autowired
    BookController controller;

    private record UserAndAccessibleBooks(LibraryUser user, List<Book> books) {}

    // Books 0-14: 3 per category (1-5), indices 0-2=cat1, 3-5=cat2, 6-8=cat3, 9-11=cat4, 12-14=cat5
    private static final List<Book> ALL_BOOKS      = DemoData.DEMO_BOOKS;
    private static final List<Book> CATEGORIES_1_2 = ALL_BOOKS.subList(0, 6);   // Children & SciFi
    private static final List<Book> CATEGORIES_3_4 = ALL_BOOKS.subList(6, 12);  // Science & Mystery
    private static final List<Book> CATEGORY_5     = ALL_BOOKS.subList(12, 15); // Classics

    private static Collection<UserAndAccessibleBooks> userSourcePermit() {
        final var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final var users   = DemoData.users(encoder);
        // boss[0] -> all, zoe[1] -> cat 1&2, bob[2] -> cat 3&4, ann[3] -> cat 5
        return List.of(
                new UserAndAccessibleBooks(users[0], ALL_BOOKS),
                new UserAndAccessibleBooks(users[1], CATEGORIES_1_2),
                new UserAndAccessibleBooks(users[2], CATEGORIES_3_4),
                new UserAndAccessibleBooks(users[3], CATEGORY_5));
    }

    private static Collection<LibraryUser> userSourceDeny() {
        final var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final var users   = DemoData.users(encoder);
        return List.of(users[4]); // pat - new intern with empty scope
    }

    @ParameterizedTest
    @MethodSource("userSourcePermit")
    void findAllPermitTest(UserAndAccessibleBooks userAndAccessibleBooks) {
        final var user           = userAndAccessibleBooks.user;
        final var expectedBooks  = userAndAccessibleBooks.books;
        final var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        final var securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        StepVerifier.create(
                Mono.deferContextual(ctx -> controller.findAll().collectList())
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        )
                .expectNextMatches(books -> books.containsAll(expectedBooks) && expectedBooks.containsAll(books))
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("userSourceDeny")
    void findAllDenyTest(LibraryUser user) {
        final var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        final var securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        StepVerifier.create(
                Mono.deferContextual(ctx -> controller.findAll().then())
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        )
                .expectError()
                .verify();
    }

}
