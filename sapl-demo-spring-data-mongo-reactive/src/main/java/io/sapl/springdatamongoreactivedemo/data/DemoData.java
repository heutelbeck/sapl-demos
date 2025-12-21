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
package io.sapl.springdatamongoreactivedemo.data;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.sapl.springdatamongoreactivedemo.domain.Book;
import io.sapl.springdatamongoreactivedemo.domain.BookRepository;
import io.sapl.springdatamongoreactivedemo.domain.LibraryUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoData {

    public static final String DEFAULT_PASSWORD = "password";

    /*
     * Library sections:
     *   1 = Children & Young Adult
     *   2 = Science Fiction & Fantasy
     *   3 = Science & Technology
     *   4 = Mystery & Thriller
     *   5 = Classics & Literature
     */
    public static final List<Book> DEMO_BOOKS = List.of(
        // @formatter:off
        // Children & Young Adult
        new Book( 1L, "The Phantom Tollbooth",              1),
        new Book( 2L, "A Wrinkle in Time",                  1),
        new Book( 3L, "The Giver",                          1),
        // Science Fiction & Fantasy
        new Book( 4L, "Neuromancer",                        2),
        new Book( 5L, "Snow Crash",                         2),
        new Book( 6L, "Elric of Melniboné",                 2),
        // Science & Technology
        new Book( 7L, "Gödel, Escher, Bach",                3),
        new Book( 8L, "Amiga Hardware Reference Manual",    3),
        new Book( 9L, "Code: The Hidden Language",          3),
        // Mystery & Thriller
        new Book(10L, "The Name of the Rose",               4),
        new Book(11L, "The Maltese Falcon",                 4),
        new Book(12L, "And Then There Were None",           4),
        // Classics & Literature
        new Book(13L, "Kafka on the Shore",                 5),
        new Book(14L, "Slaughterhouse-Five",                5),
        new Book(15L, "The Master and Margarita",           5)
        // @formatter:on
    );

    private final BookRepository  bookRepository;
    private final PasswordEncoder encoder;

    /*
     * Library staff and their assigned sections:
     *   boss - Head librarian (all sections)
     *   zoe  - Children & SciFi specialist
     *   bob  - Science & Mystery sections
     *   ann  - Classics curator
     *   pat  - New intern (no sections assigned yet - access denied)
     */
    public static LibraryUser[] users(PasswordEncoder encoder) {
        // @formatter:off
        return new LibraryUser[] {
            new LibraryUser("boss", 0, List.of(1,2,3,4,5), encoder.encode(DEFAULT_PASSWORD)),
            new LibraryUser("zoe",  1, List.of(1,2),       encoder.encode(DEFAULT_PASSWORD)),
            new LibraryUser("bob",  2, List.of(3,4),       encoder.encode(DEFAULT_PASSWORD)),
            new LibraryUser("ann",  3, List.of(5),         encoder.encode(DEFAULT_PASSWORD)),
            new LibraryUser("pat",  4, List.of(),          encoder.encode(DEFAULT_PASSWORD)),
        };
        // @formatter:on
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadDemoData() {
        log.info("Loading demo book collection...");
        bookRepository.deleteAll()
                .thenMany(bookRepository.saveAll(DEMO_BOOKS))
                .blockLast();
        log.info("Demo data loaded successfully.");
    }

}
