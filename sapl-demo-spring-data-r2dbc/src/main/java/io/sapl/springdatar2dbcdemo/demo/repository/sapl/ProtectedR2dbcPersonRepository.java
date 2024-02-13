package io.sapl.springdatar2dbcdemo.demo.repository.sapl;

import io.sapl.springdatar2dbc.sapl.utils.annotation.EnforceR2dbc;
import io.sapl.springdatar2dbc.sapl.utils.annotation.SaplProtectedR2dbc;
import io.sapl.springdatar2dbcdemo.demo.repository.ClassForAnnotation;
import io.sapl.springdatar2dbcdemo.demo.repository.Person;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
@SaplProtectedR2dbc
public interface ProtectedR2dbcPersonRepository extends R2dbcRepository<Person, Integer>, CustomR2dbcPersonRepository<Person, Integer> {

    Flux<Person> findAllByAgeAfter(Integer age);

    @Query("SELECT * FROM person WHERE lastname LIKE CONCAT('%', (:lastnameContains), '%')")
    Flux<Person> fetchingByQueryMethodLastnameContains(String lastnameContains);

    @EnforceR2dbc(action = "custom_repository_method")
    Flux<Person> customRepositoryMethod();

    @EnforceR2dbc(
            action = "find_all_by_age",
            subject = "@serviceForAnnotation.setSubject(#active)",
            resource = "#setResource(#age)",
            environment = "T(io.sapl.springdatar2dbcdemo.demo.repository.ClassForAnnotation).setEnvironment(#active)",
            staticClasses = {ClassForAnnotation.class})
    Flux<Person> findAllByAgeAfterAndActive(int age, boolean active);

}

