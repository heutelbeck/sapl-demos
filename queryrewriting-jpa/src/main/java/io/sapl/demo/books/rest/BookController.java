package io.sapl.demo.books.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.demo.books.domain.Book;
import io.sapl.demo.books.domain.BookRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookRepository repository;

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    List<Book> findAll() {
        return repository.findAll(Optional.empty());
    }

}
