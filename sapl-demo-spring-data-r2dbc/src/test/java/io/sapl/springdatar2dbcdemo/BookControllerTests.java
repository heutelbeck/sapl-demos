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
package io.sapl.springdatar2dbcdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.sapl.springdatar2dbcdemo.rest.BookController;
import reactor.test.StepVerifier;

@SpringBootTest
class BookControllerTests {

    @Autowired
    BookController controller;

    @Test
    @WithMockLibraryUser(username = "admin", dataScope = {})
    void adminWithEmptyDataScopeSeesAllBooks() {
        StepVerifier.create(controller.findAll().collectList())
                .expectNextMatches(books -> books.size() == 6)
                .verifyComplete();
    }

    @Test
    @WithMockLibraryUser(username = "tom", dataScope = {1, 2, 3})
    void tomWithDataScope123SeesFilteredBooks() {
        StepVerifier.create(controller.findAll().collectList())
                .expectNextMatches(books -> books.size() == 4
                    && books.stream().allMatch(b -> b.getCategory() <= 3))
                .verifyComplete();
    }

    @Test
    @WithMockLibraryUser(username = "sim", dataScope = {1, 2})
    void simWithDataScope12SeesFilteredBooks() {
        StepVerifier.create(controller.findAll().collectList())
                .expectNextMatches(books -> books.size() == 3
                    && books.stream().allMatch(b -> b.getCategory() <= 2))
                .verifyComplete();
    }

}
