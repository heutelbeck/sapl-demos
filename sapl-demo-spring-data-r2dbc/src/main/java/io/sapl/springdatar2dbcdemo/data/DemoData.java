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
package io.sapl.springdatar2dbcdemo.data;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.sapl.springdatar2dbcdemo.domain.Book;
import io.sapl.springdatar2dbcdemo.domain.LibraryUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoData {

    public static final String DEFAULT_PASSWORD = "password";

    public static final List<Book> DEMO_BOOKS = List.of(
            new Book(1L, "book1", 1),
            new Book(2L, "book2", 1),
            new Book(3L, "book3", 2),
            new Book(4L, "book4", 3),
            new Book(5L, "book5", 4),
            new Book(6L, "book6", 5)
    );

    private final DatabaseClient  databaseClient;
    private final PasswordEncoder encoder;

    public static LibraryUser[] users(PasswordEncoder encoder) {
        return new LibraryUser[] {
            new LibraryUser("admin", 1, List.of(),        encoder.encode(DEFAULT_PASSWORD)),
            new LibraryUser("tom",   1, List.of(1, 2, 3), encoder.encode(DEFAULT_PASSWORD)),
            new LibraryUser("sim",   2, List.of(1, 2),    encoder.encode(DEFAULT_PASSWORD)),
            new LibraryUser("kat",   3, null,             encoder.encode(DEFAULT_PASSWORD)),
        };
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadDemoData() {
        log.info("Creating book table...");
        databaseClient.sql("CREATE TABLE IF NOT EXISTS book (id BIGINT PRIMARY KEY, name VARCHAR(255), category INT)")
                .then()
                .block();

        log.info("Loading demo book collection...");
        for (var book : DEMO_BOOKS) {
            databaseClient.sql("INSERT INTO book (id, name, category) VALUES (:id, :name, :category)")
                    .bind("id", book.getId())
                    .bind("name", book.getName())
                    .bind("category", book.getCategory())
                    .then()
                    .block();
        }
        log.info("Demo data loaded successfully.");
    }

}
