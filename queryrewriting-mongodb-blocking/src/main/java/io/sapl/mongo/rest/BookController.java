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

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.mongo.domain.Book;
import io.sapl.mongo.domain.BookQueryService;
import io.sapl.mongo.domain.BookQueryService.BookView;
import io.sapl.mongo.domain.BookQueryService.ProvenanceView;
import io.sapl.mongo.domain.BookRepository;
import lombok.RequiredArgsConstructor;

/**
 * Exposes every MongoDB shim hook as an endpoint so the effect of the query
 * rewrite can be observed (and integration-tested) on each. They are all guarded
 * by the same {@code findAll} policy; the obligation narrows {@code category} to
 * the caller's data scope no matter which template API is used.
 */
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookRepository   repository;
    private final BookQueryService queryService;

    // Repository path (the surface SimpleMongoRepository bottoms out on).

    @GetMapping("/")
    public List<Book> findAll() {
        return repository.findAllBooks();
    }

    @GetMapping("/repository/by-category-floor")
    public List<Book> byCategoryFloor(@RequestParam(defaultValue = "1") int floor) {
        return repository.findByCategoryGreaterThanEqual(floor);
    }

    @GetMapping("/repository/count")
    public long repositoryCount(@RequestParam(defaultValue = "1") int floor) {
        return repository.countByCategoryGreaterThanEqual(floor);
    }

    // Legacy template path.

    @GetMapping("/template/find")
    public List<Book> legacyFind() {
        return queryService.legacyFindAll();
    }

    // Fluent find chain.

    @GetMapping("/fluent/all")
    public List<Book> fluentAll() {
        return queryService.fluentAll();
    }

    @GetMapping("/fluent/count")
    public long fluentCount() {
        return queryService.fluentCount();
    }

    @GetMapping("/fluent/named")
    public List<Book> fluentNamed(@RequestParam(defaultValue = ".*") String pattern) {
        return queryService.fluentNamedLike(pattern);
    }

    @GetMapping("/fluent/titles")
    public List<BookView> fluentTitles() {
        return queryService.fluentTitles();
    }

    // Fluent write builders (selection narrowed).

    @PostMapping("/fluent/review")
    public long markReviewed() {
        return queryService.markReviewed();
    }

    @DeleteMapping("/fluent")
    public long purge() {
        return queryService.purge();
    }

    // Narrowing variants: same template APIs, different policies (one obligation shape each).

    @GetMapping("/open-shelf")
    public List<Book> openShelf() {
        return queryService.openShelf();
    }

    @GetMapping("/age-appropriate")
    public List<Book> ageAppropriate() {
        return queryService.ageAppropriate();
    }

    @GetMapping("/provenance")
    public List<ProvenanceView> provenance() {
        return queryService.provenance();
    }

}
