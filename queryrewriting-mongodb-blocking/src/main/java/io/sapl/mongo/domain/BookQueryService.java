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
package io.sapl.mongo.domain;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import io.sapl.spring.method.metadata.PreEnforce;
import lombok.RequiredArgsConstructor;

/**
 * Demonstrates that the SAPL MongoDB shim narrows every data-reaching entry
 * point of {@link MongoTemplate}, not just the repository path. Every method is
 * enforced by the same {@code findAll} policy, whose obligation restricts
 * {@code category} to the caller's data scope. The shim applies that obligation
 * regardless of which template API the method uses (legacy find, the fluent find
 * chain, or the fluent update / remove builders), so a caller only ever reads or
 * writes the books they are allowed to see.
 */
@Service
@RequiredArgsConstructor
public class BookQueryService {

    private final MongoTemplate template;

    /** Legacy template find with an explicit (empty) query. */
    @PreEnforce(action = "'findAll'")
    public List<Book> legacyFindAll() {
        return template.find(new Query(), Book.class);
    }

    /** Fluent chain, bare terminal with no matching() step. */
    @PreEnforce(action = "'findAll'")
    public List<Book> fluentAll() {
        return template.query(Book.class).all();
    }

    /** Fluent chain count terminal. */
    @PreEnforce(action = "'findAll'")
    public long fluentCount() {
        return template.query(Book.class).count();
    }

    /** Fluent chain with a user-supplied Criteria, intersected with the obligation. */
    @PreEnforce(action = "'findAll'")
    public List<Book> fluentNamedLike(String pattern) {
        return template.query(Book.class).matching(Criteria.where("name").regex(pattern)).all();
    }

    /** Fluent chain with a projection; narrowed before projection. */
    @PreEnforce(action = "'findAll'")
    public List<BookView> fluentTitles() {
        return template.query(Book.class).as(BookView.class).all();
    }

    /** Fluent update builder; the selection is narrowed, so only in-scope books are touched. */
    @PreEnforce(action = "'findAll'")
    public long markReviewed() {
        return template.update(Book.class).apply(new Update().set("reviewed", true)).all().getModifiedCount();
    }

    /** Fluent remove builder; the selection is narrowed, so only in-scope books are deleted. */
    @PreEnforce(action = "'findAll'")
    public long purge() {
        return template.remove(Book.class).all().getDeletedCount();
    }

    /*
     * The methods below keep the same template APIs but trigger different policies
     * (one per action), so each demonstrates a different obligation criteria shape.
     */

    /**
     * Open-shelf browsing. The obligation uses nested {@code and}/{@code or} with
     * {@code in}, {@code =} and {@code !=}: a patron sees unrestricted books in their
     * sections, plus any book they personally curate.
     */
    @PreEnforce(action = "'browseOpenShelf'")
    public List<Book> openShelf() {
        return template.query(Book.class).all();
    }

    /**
     * Age-appropriate browsing. The obligation uses {@code <=} to compare each book's
     * minimum age against the patron's age, so only borrowable titles come back.
     */
    @PreEnforce(action = "'browseAgeAppropriate'")
    public List<Book> ageAppropriate() {
        return template.find(new Query(), Book.class);
    }

    /**
     * Cataloguing provenance view. The obligation uses {@code isNotNull} plus a raw
     * extended-JSON {@code $regex} condition: only fully catalogued records (a curator
     * is assigned) classified under Literature (call number 8xx).
     */
    @PreEnforce(action = "'auditProvenance'")
    public List<ProvenanceView> provenance() {
        return template.query(Book.class).as(ProvenanceView.class).all();
    }

    /** Projection view for the {@code as(..)} fluent step. */
    public record BookView(String name, Integer category) {}

    /** Projection view for the cataloguing provenance endpoint. */
    public record ProvenanceView(String name, String curator, String callNumber) {}
}
