package io.sapl.demo.books.data;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.sapl.demo.books.domain.Book;
import io.sapl.demo.books.domain.BookRepository;
import io.sapl.demo.books.domain.LibraryUser;
import io.sapl.demo.books.security.LibraryUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoData implements CommandLineRunner {

	public static final String DEFAULT_PASSWORD = "password";

	private static final String NAME_ADMIN = "admin";
	private static final String NAME_TOM = "Tom";
	private static final String NAME_SIM = "Sim";
	private static final String NAME_KAT = "Kat";

	public static final LibraryUser[] DEMO_USERS = new LibraryUser[] {
			// @formatter:off
			new LibraryUser(NAME_ADMIN, 1, List.of(),      DEFAULT_PASSWORD),
			new LibraryUser(NAME_TOM,   1, List.of(1,2,3), DEFAULT_PASSWORD),
			new LibraryUser(NAME_SIM,   2, List.of(1,2),   DEFAULT_PASSWORD),
			new LibraryUser(NAME_KAT,   3, null,           DEFAULT_PASSWORD)
			// @formatter:on
	};

	private final BookRepository bookRepository;
	private final LibraryUserDetailsService userDetailsService;
	private final PasswordEncoder encoder;

	@Override
	public void run(String... args) {
		log.info("Loading demo book collection...");
		bookRepository.save(new Book(1L, "book1", 1));
		bookRepository.save(new Book(2L, "book2", 1));
		bookRepository.save(new Book(3L, "book3", 2));
		bookRepository.save(new Book(4L, "book4", 3));
		bookRepository.save(new Book(5L, "book5", 4));
		bookRepository.save(new Book(6L, "book6", 5));

		log.info("Loading demo users...");
		for (var demoUser : DEMO_USERS)
			userDetailsService.load(new LibraryUser(demoUser.getUsername(), demoUser.getDepartment(),
					demoUser.getDataScope(), encoder.encode(DEFAULT_PASSWORD)));
	}

}
