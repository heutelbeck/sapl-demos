package io.sapl.springdatamongoreactivedemo.demo.repository.sapl;

import io.sapl.springdatamongoreactive.sapl.utils.annotation.EnforceMongoReactive;
import io.sapl.springdatamongoreactive.sapl.utils.annotation.SaplProtectedMongoReactive;
import io.sapl.springdatamongoreactivedemo.demo.repository.ClassForAnnotation;
import io.sapl.springdatamongoreactivedemo.demo.repository.Role;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
@SaplProtectedMongoReactive
public interface ProtectedReactiveMongoUserRepository extends ReactiveCrudRepository<User, Integer> , CustomReactiveMongoUserRepository<User, Integer> {

    Flux<User> findAllByAgeAfter(Integer age);

    @Query("{'lastname': {'$regex': ?0}}")
    Flux<User> fetchingByQueryMethodLastnameContains(String lastnameContains);

    @EnforceMongoReactive(action = "custom_repository_method")
    Flux<User> customRepositoryMethod();

    @EnforceMongoReactive(
            action = "find_all_by_age",
            subject = "@serviceForAnnotation.setSubject(#role)",
            resource = "#setResource(#age)",
            environment = "T(io.sapl.springdatamongoreactivedemo.demo.repository.ClassForAnnotation).setEnvironment(#role)",
            staticClasses = {ClassForAnnotation.class})
    Flux<User> findAllByAgeAfterAndRole(int age, Role role);
}
