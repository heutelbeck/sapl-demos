package io.sapl.geo.demo.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GeoUserRepository extends ReactiveCrudRepository<GeoUser, Integer> {
	Mono<GeoUser> findByUsername(String username);
}


