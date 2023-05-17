package io.sapl.demo.books.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.sapl.spring.method.metadata.PreEnforce;

public interface BookRepository extends CrudRepository<Book, Long> {

	@PreEnforce
	// @formatter:off
	@Query("SELECT book FROM Book book"
	     + " WHERE :#{#filter == null} = true"
		 + " OR book.category IN :filter")
	// @formatter:on
	List<Book> findAll(@Param("filter") Optional<Collection<Integer>> filter);

}
