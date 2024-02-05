package io.sapl.springdatamongoreactivedemo.demo.repository.sapl;

import reactor.core.publisher.Flux;

public interface CustomReactiveMongoUserRepository<T, ID> {

    Flux<T> customRepositoryMethod();
}

