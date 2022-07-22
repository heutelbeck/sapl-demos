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

	private static final String NAME_ADMIN = "admin";
	private static final String NAME_TOM = "Tom";
	private static final String NAME_SIM = "Sim";
	private static final String NAME_KAT = "Kat";
	private static final String DEFAULT_PASSWORD = "password";
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
		// @formatter:off
		userDetailsService.load(new LibraryUser(NAME_ADMIN, 1, List.of(),      encoder.encode(DEFAULT_PASSWORD)));
		userDetailsService.load(new LibraryUser(NAME_TOM,   1, List.of(1,2,3), encoder.encode(DEFAULT_PASSWORD)));
		userDetailsService.load(new LibraryUser(NAME_SIM,   2, List.of(1,2),   encoder.encode(DEFAULT_PASSWORD)));
		userDetailsService.load(new LibraryUser(NAME_KAT,   3, null,           encoder.encode(DEFAULT_PASSWORD)));
		// @formatter:on
	}

}
