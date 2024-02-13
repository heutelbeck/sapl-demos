package io.sapl.springdatamongoreactivedemo.demo.repository.unprotected;

import io.sapl.springdatamongoreactivedemo.demo.repository.Role;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import io.sapl.springdatamongoreactivedemo.demo.repository.sapl.CustomReactiveMongoUserRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface ReactiveMongoUserRepository extends ReactiveCrudRepository<User, String>, CustomReactiveMongoUserRepository<User, Integer> {

    Flux<User> findAllByAgeAfter(Integer age);

    @Query("{'lastname': {'$regex': ?0}}")
    Flux<User> fetchingByQueryMethodLastnameContains(String lastnameContains);

    Flux<User> customRepositoryMethod();

    Flux<User> findAllByAgeAfterAndRole(int age, Role role);
}
