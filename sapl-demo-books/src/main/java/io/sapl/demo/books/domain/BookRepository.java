package io.sapl.demo.books.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.sapl.spring.method.metadata.PreEnforce;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

	@PreEnforce
	@Query("select b from Book b where :filter is null or b.category in :filter")
	List<Book> findAll(@Param("filter") Optional<Collection<Integer>> filter);

}
