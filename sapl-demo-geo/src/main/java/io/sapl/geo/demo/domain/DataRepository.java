package io.sapl.geo.demo.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.sapl.spring.method.metadata.PreEnforce;
import reactor.core.publisher.Flux;

public interface DataRepository extends ReactiveCrudRepository<Data, Integer>  {
	
	@Override
	@PreEnforce
	public Flux<Data> findAll();
}
