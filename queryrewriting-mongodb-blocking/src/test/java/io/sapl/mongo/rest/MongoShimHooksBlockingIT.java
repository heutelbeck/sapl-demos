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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import io.sapl.mongo.data.DemoData;
import io.sapl.mongo.domain.Book;
import io.sapl.mongo.domain.BookQueryService.ProvenanceView;
import io.sapl.mongo.domain.LibraryUser;

/**
 * Verifies the effect of the SAPL query rewrite on every shim hook exposed by
 * {@link BookController}: the repository path, the legacy template path, the
 * fluent find chain (bare terminals, criteria, projection) and the fluent update
 * / remove builders. The {@code findAll} policy attaches an obligation that
 * restricts {@code category} to the caller's data scope, so for a user scoped to
 * categories 1 and 2 every endpoint sees exactly the six in-scope books, never
 * the other nine.
 */
@SpringBootTest
@Testcontainers
@DisplayName("MongoDB shim narrows every endpoint (blocking)")
class MongoShimHooksBlockingIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    // zoe's scope: Children & Young Adult (1) and Science Fiction & Fantasy (2) = 6 books.
    private static final List<Integer> ZOE_SCOPE    = List.of(1, 2);
    private static final int           ZOE_IN_SCOPE = 6;
    private static final List<Integer> BOSS_SCOPE   = List.of(1, 2, 3, 4, 5);
    private static final int           ALL_BOOKS    = 15;

    @Autowired
    BookController controller;

    @Autowired
    MongoTemplate template;

    @BeforeEach
    void resetCollection() {
        // No enforcement plan is in scope here, so the shim is a pass-through and the
        // collection is fully reset regardless of any obligation.
        template.remove(new Query(), Book.class);
        template.insertAll(DemoData.DEMO_BOOKS);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static LibraryUser user(List<Integer> scope) {
        return user("test-user", scope, 99);
    }

    private static LibraryUser user(String username, List<Integer> scope, int age) {
        return new LibraryUser(username, 0, scope, age, List.of());
    }

    private static List<Long> idsOf(List<Book> books) {
        return books.stream().map(Book::getId).toList();
    }

    private static List<Long> expectedIds(Predicate<Book> predicate) {
        return DemoData.DEMO_BOOKS.stream().filter(predicate).map(Book::getId).toList();
    }

    private <T> T asUser(LibraryUser user, Supplier<T> call) {
        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.authorities());
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        return call.get();
    }

    @Test
    @DisplayName("repository findAll is narrowed to the caller's categories")
    void repositoryFindAll() {
        var books = asUser(user(ZOE_SCOPE), controller::findAll);
        assertThat(books).hasSize(ZOE_IN_SCOPE).allMatch(b -> ZOE_SCOPE.contains(b.getCategory()));
    }

    @Test
    @DisplayName("repository derived query is intersected with the obligation")
    void repositoryDerived() {
        var books = asUser(user(ZOE_SCOPE), () -> controller.byCategoryFloor(1));
        assertThat(books).hasSize(ZOE_IN_SCOPE).allMatch(b -> ZOE_SCOPE.contains(b.getCategory()));
    }

    @Test
    @DisplayName("repository count is narrowed")
    void repositoryCount() {
        assertThat(asUser(user(ZOE_SCOPE), () -> controller.repositoryCount(1))).isEqualTo((long) ZOE_IN_SCOPE);
    }

    @Test
    @DisplayName("legacy template find is narrowed")
    void legacyFind() {
        var books = asUser(user(ZOE_SCOPE), controller::legacyFind);
        assertThat(books).hasSize(ZOE_IN_SCOPE).allMatch(b -> ZOE_SCOPE.contains(b.getCategory()));
    }

    @Test
    @DisplayName("fluent all() with no matching step is narrowed")
    void fluentAll() {
        var books = asUser(user(ZOE_SCOPE), controller::fluentAll);
        assertThat(books).hasSize(ZOE_IN_SCOPE).allMatch(b -> ZOE_SCOPE.contains(b.getCategory()));
    }

    @Test
    @DisplayName("fluent count() is narrowed")
    void fluentCount() {
        assertThat(asUser(user(ZOE_SCOPE), controller::fluentCount)).isEqualTo((long) ZOE_IN_SCOPE);
    }

    @Test
    @DisplayName("fluent matching(Criteria) is intersected with the obligation")
    void fluentNamed() {
        var books = asUser(user(ZOE_SCOPE), () -> controller.fluentNamed(".*"));
        assertThat(books).hasSize(ZOE_IN_SCOPE).allMatch(b -> ZOE_SCOPE.contains(b.getCategory()));
    }

    @Test
    @DisplayName("fluent as(View) projection is narrowed before projection")
    void fluentTitles() {
        var views = asUser(user(ZOE_SCOPE), controller::fluentTitles);
        assertThat(views).hasSize(ZOE_IN_SCOPE).allMatch(v -> ZOE_SCOPE.contains(v.category()));
    }

    @Test
    @DisplayName("fluent update builder only touches in-scope books")
    void fluentUpdateNarrowed() {
        assertThat(asUser(user(ZOE_SCOPE), controller::markReviewed)).isEqualTo((long) ZOE_IN_SCOPE);
    }

    @Test
    @DisplayName("fluent remove builder only deletes in-scope books")
    void fluentRemoveNarrowed() {
        assertThat(asUser(user(ZOE_SCOPE), controller::purge)).isEqualTo((long) ZOE_IN_SCOPE);
        // The other nine books survive: a fresh unscoped read still finds them.
        assertThat(template.find(new Query(), Book.class)).hasSize(ALL_BOOKS - ZOE_IN_SCOPE);
    }

    @Test
    @DisplayName("a full-scope user is not restricted on any endpoint")
    void fullScopeSeesEverything() {
        assertThat(asUser(user(BOSS_SCOPE), controller::fluentAll)).hasSize(ALL_BOOKS);
        assertThat(asUser(user(BOSS_SCOPE), controller::fluentCount)).isEqualTo((long) ALL_BOOKS);
    }

    @Test
    @DisplayName("a user with an empty scope is denied on every path")
    void emptyScopeDenied() {
        var denied = user(List.of());
        assertThatThrownBy(() -> asUser(denied, controller::fluentAll)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> asUser(denied, controller::fluentCount)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> asUser(denied, controller::findAll)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("open shelf: compound and/or shows unrestricted in-scope books plus the caller's own curated ones")
    void openShelfCuratorStillSeesRestricted() {
        var bob   = user("bob", List.of(3, 4), 40);
        var books = asUser(bob, controller::openShelf);
        Predicate<Book> visibleToBob = b -> List.of(3, 4).contains(b.getCategory()) && !b.getRestricted()
                || "bob".equals(b.getCurator());
        assertThat(idsOf(books)).containsExactlyInAnyOrderElementsOf(expectedIds(visibleToBob)).contains(7L, 8L);
    }

    @Test
    @DisplayName("open shelf: a non-curator never sees restricted stock")
    void openShelfNonCuratorCannotSeeRestricted() {
        var guest = user("guest", List.of(3, 4), 40);
        var books = asUser(guest, controller::openShelf);
        assertThat(books).allMatch(b -> List.of(3, 4).contains(b.getCategory()) && !b.getRestricted());
        assertThat(idsOf(books)).doesNotContain(7L, 8L);
    }

    @Test
    @DisplayName("age-appropriate: <= comparison hides books above the patron's age")
    void ageAppropriateNarrowsByMinimumAge() {
        var child = user("child", List.of(), 12);
        var books = asUser(child, controller::ageAppropriate);
        assertThat(books).allMatch(b -> b.getMinimumAge() <= 12);
        assertThat(idsOf(books)).containsExactlyInAnyOrderElementsOf(expectedIds(b -> b.getMinimumAge() <= 12));
    }

    @Test
    @DisplayName("age-appropriate: an adult patron can borrow everything")
    void ageAppropriateAdultSeesEverything() {
        var adult = user("adult", List.of(), 99);
        assertThat(asUser(adult, controller::ageAppropriate)).hasSize(ALL_BOOKS);
    }

    @Test
    @DisplayName("provenance: isNotNull plus raw regex shows only curated Literature (8xx)")
    void provenanceShowsOnlyCuratedLiterature() {
        var staff = user("curator", List.of(1, 2, 3, 4, 5), 99);
        var views = asUser(staff, controller::provenance);
        var expectedNames = DemoData.DEMO_BOOKS.stream()
                .filter(b -> b.getCurator() != null && b.getCallNumber().startsWith("8")).map(Book::getName).toList();
        assertThat(views).allSatisfy(v -> {
            assertThat(v.curator()).isNotNull();
            assertThat(v.callNumber()).startsWith("8");
        });
        assertThat(views).map(ProvenanceView::name).containsExactlyInAnyOrderElementsOf(expectedNames);
    }
}
