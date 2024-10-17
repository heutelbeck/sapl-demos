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

    private static final Book[] ALL_CATEGORIES    = DemoData.DEMO_BOOKS.toArray(new Book[DemoData.DEMO_BOOKS.size()]);
    private static final Book[] CATEGORIES_1_TO_3 = new Book[] { DemoData.DEMO_BOOKS.get(0), DemoData.DEMO_BOOKS.get(1),
            DemoData.DEMO_BOOKS.get(2), DemoData.DEMO_BOOKS.get(3) };
    private static final Book[] CATEGORIES_1_TO_2 = new Book[] { DemoData.DEMO_BOOKS.get(0), DemoData.DEMO_BOOKS.get(1),
            DemoData.DEMO_BOOKS.get(2) };

    @Autowired
    BookController controller;

    private static Collection<UserAndAccessibleBooks> userSourcePermit() {
        final var users          = DemoData.users(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        final var permittedUsers = new UserAndAccessibleBooks[] {
                new UserAndAccessibleBooks(users[0], List.of(ALL_CATEGORIES)),
                new UserAndAccessibleBooks(users[1], List.of(CATEGORIES_1_TO_3)),
                new UserAndAccessibleBooks(users[2], List.of(CATEGORIES_1_TO_2)) };
        return List.of(permittedUsers);
    }

    private static Collection<LibraryUser> userSourceDeny() {
        final var users = DemoData.users(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        return List.of(users[3]);
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
