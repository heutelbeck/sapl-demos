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
package io.sapl.mongo.data;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.sapl.mongo.domain.Book;
import io.sapl.mongo.domain.BookRepository;
import io.sapl.mongo.domain.LibraryUser;
import io.sapl.mongo.domain.LibraryUserDetails;
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
    /*
     * Columns: id, title, section, restricted, minimumAge, curator, callNumber.
     * restricted marks rare or reference-only stock; curator is the staff member
     * who maintains the record (null when unassigned); callNumber is a Dewey-style
     * classification (8xx is Literature).
     */
    public static final List<Book> DEMO_BOOKS = List.of(
        // @formatter:off
        // Children & Young Adult (section 1)
        new Book( 1L, "The Phantom Tollbooth",           1, false,  0, "zoe",  "028.5"),
        new Book( 2L, "A Wrinkle in Time",               1, false,  0, "zoe",  "813.54"),
        new Book( 3L, "The Giver",                       1, false, 12, null,   "813.54"),
        // Science Fiction & Fantasy (section 2)
        new Book( 4L, "Neuromancer",                     2, false, 16, "zoe",  "813.54"),
        new Book( 5L, "Snow Crash",                      2, false, 16, null,   "813.54"),
        new Book( 6L, "Elric of Melnibone",              2, false, 12, "zoe",  "823.91"),
        // Science & Technology (section 3)
        new Book( 7L, "Godel, Escher, Bach",             3, true,  16, "bob",  "510.1"),
        new Book( 8L, "Amiga Hardware Reference Manual", 3, true,   0, "bob",  "004.16"),
        new Book( 9L, "Code: The Hidden Language",       3, false, 12, null,   "005.1"),
        // Mystery & Thriller (section 4)
        new Book(10L, "The Name of the Rose",            4, false, 18, "bob",  "853.914"),
        new Book(11L, "The Maltese Falcon",              4, false, 16, null,   "813.52"),
        new Book(12L, "And Then There Were None",        4, false, 12, "bob",  "823.912"),
        // Classics & Literature (section 5)
        new Book(13L, "Kafka on the Shore",              5, true,  18, "ann",  "895.6"),
        new Book(14L, "Slaughterhouse-Five",             5, false, 18, "ann",  "813.54"),
        new Book(15L, "The Master and Margarita",        5, false, 16, null,   "891.73")
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
    public static LibraryUserDetails[] users(PasswordEncoder encoder) {
        var encodedPassword = encoder.encode(DEFAULT_PASSWORD);
        // @formatter:off
        return new LibraryUserDetails[] {
            new LibraryUserDetails(new LibraryUser("boss", 0, List.of(1,2,3,4,5), 45, List.of()), encodedPassword),
            new LibraryUserDetails(new LibraryUser("zoe",  1, List.of(1,2),       30, List.of()), encodedPassword),
            new LibraryUserDetails(new LibraryUser("bob",  2, List.of(3,4),       30, List.of()), encodedPassword),
            new LibraryUserDetails(new LibraryUser("ann",  3, List.of(5),         60, List.of()), encodedPassword),
            new LibraryUserDetails(new LibraryUser("pat",  4, List.of(),          16, List.of()), encodedPassword),
        };
        // @formatter:on
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadDemoData() {
        log.info("Loading demo book collection...");
        bookRepository.deleteAll();
        bookRepository.saveAll(DEMO_BOOKS);
        log.info("Demo data loaded successfully.");
    }

}
