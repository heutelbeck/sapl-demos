package io.sapl.springdatar2dbcdemo.demo.repository.unprotected;

import io.sapl.springdatar2dbcdemo.demo.repository.Person;
import io.sapl.springdatar2dbcdemo.demo.repository.sapl.CustomR2dbcPersonRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface R2dbcPersonRepository extends R2dbcRepository<Person, Integer>, CustomR2dbcPersonRepository<Person, Integer> {

    Flux<Person> findAllByAgeAfter(Integer age);

    @Query("SELECT * FROM person WHERE lastname LIKE CONCAT('%', (:lastnameContains), '%')")
    Flux<Person> fetchingByQueryMethodLastnameContains(String lastnameContains);

    Flux<Person> customRepositoryMethod();

    Flux<Person> findAllByAgeAfterAndActive(int age, boolean active);
}

