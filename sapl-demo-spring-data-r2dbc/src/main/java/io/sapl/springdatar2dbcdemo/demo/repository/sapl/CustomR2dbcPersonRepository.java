package io.sapl.springdatar2dbcdemo.demo.repository.sapl;

import reactor.core.publisher.Flux;

public interface CustomR2dbcPersonRepository<T, ID> {

    Flux<T> customRepositoryMethod();

}

