package io.sapl.demo.books.rest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.sapl.demo.books.SaplDemoBooksApplication;
import io.sapl.demo.books.data.DemoData;
import io.sapl.demo.books.domain.Book;
import io.sapl.demo.books.domain.LibraryUser;

@SpringJUnitConfig
@SpringBootTest(classes = SaplDemoBooksApplication.class)
class SaplDemoBooksApplicationTests {

    @Autowired
    DemoData demoData;

    private record UserAndAccessibleBooks(LibraryUser user, List<Book> books) {
    }

    // Books 0-14: 3 per category (1-5), indices 0-2=cat1, 3-5=cat2, 6-8=cat3, 9-11=cat4, 12-14=cat5
    private static final List<Book> ALL_BOOKS      = DemoData.DEMO_BOOKS;
    private static final List<Book> CATEGORIES_1_2 = ALL_BOOKS.subList(0, 6);   // Children & SciFi
    private static final List<Book> CATEGORIES_3_4 = ALL_BOOKS.subList(6, 12);  // Science & Mystery
    private static final List<Book> CATEGORY_5     = ALL_BOOKS.subList(12, 15); // Classics

    @Autowired
    BookController controller;

    private static Collection<UserAndAccessibleBooks> userSourcePermit() {
        final var users = DemoData.users(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        // boss[0] -> all, zoe[1] -> cat 1&2, bob[2] -> cat 3&4, ann[3] -> cat 5
        return List.of(
                new UserAndAccessibleBooks(users[0], ALL_BOOKS),
                new UserAndAccessibleBooks(users[1], CATEGORIES_1_2),
                new UserAndAccessibleBooks(users[2], CATEGORIES_3_4),
                new UserAndAccessibleBooks(users[3], CATEGORY_5));
    }

    private static Collection<LibraryUser> userSourceDeny() {
        final var users = DemoData.users(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        return List.of(users[4]); // pat - new intern with empty scope
    }

    @ParameterizedTest
    @MethodSource("userSourcePermit")
    void findAllPermitTest(UserAndAccessibleBooks userAndAccessibleBooks) {
        final var user           = userAndAccessibleBooks.user;
        final var expectedBooks  = userAndAccessibleBooks.books;
        final var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final var actualBooks = assertDoesNotThrow(() -> controller.findAll());
        assertEquals(expectedBooks, actualBooks);
    }

    @ParameterizedTest
    @MethodSource("userSourceDeny")
    void findAllDenyTest(LibraryUser user) {
        final var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThrows(AccessDeniedException.class, () -> controller.findAll());
    }

}
