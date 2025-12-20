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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.sapl.springdatamongoreactivedemo.data.DemoData;
import io.sapl.springdatamongoreactivedemo.domain.Book;
import io.sapl.springdatamongoreactivedemo.domain.LibraryUser;
import io.sapl.springdatamongoreactivedemo.rest.BookController;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringJUnitConfig
@SpringBootTest(classes = SaplSpringDataMongoReactiveDemoApplication.class)
class BookControllerTests {

    @Autowired
    BookController controller;

    @Autowired
    PasswordEncoder encoder;

    private record UserAndAccessibleBooks(LibraryUser user, List<Book> books) {}

    private static final Book[] ALL_CATEGORIES    = DemoData.DEMO_BOOKS.toArray(new Book[0]);
    private static final Book[] CATEGORIES_1_TO_3 = new Book[] {
            DemoData.DEMO_BOOKS.get(0), DemoData.DEMO_BOOKS.get(1),
            DemoData.DEMO_BOOKS.get(2), DemoData.DEMO_BOOKS.get(3)
    };
    private static final Book[] CATEGORIES_1_TO_2 = new Book[] {
            DemoData.DEMO_BOOKS.get(0), DemoData.DEMO_BOOKS.get(1),
            DemoData.DEMO_BOOKS.get(2)
    };

    private static Collection<UserAndAccessibleBooks> userSourcePermit() {
        final var encoder        = org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final var users          = DemoData.users(encoder);
        final var permittedUsers = new UserAndAccessibleBooks[] {
                new UserAndAccessibleBooks(users[0], List.of(ALL_CATEGORIES)),
                new UserAndAccessibleBooks(users[1], List.of(CATEGORIES_1_TO_3)),
                new UserAndAccessibleBooks(users[2], List.of(CATEGORIES_1_TO_2))
        };
        return List.of(permittedUsers);
    }

    private static Collection<LibraryUser> userSourceDeny() {
        final var encoder = org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final var users   = DemoData.users(encoder);
        return List.of(users[3]);
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
