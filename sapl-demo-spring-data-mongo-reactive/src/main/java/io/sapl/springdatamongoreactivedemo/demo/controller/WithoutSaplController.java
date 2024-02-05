package io.sapl.springdatamongoreactivedemo.demo.controller;

import io.sapl.springdatamongoreactivedemo.demo.repository.Role;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import io.sapl.springdatamongoreactivedemo.demo.repository.unprotected.ReactiveMongoUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Objects;


@Controller
@AllArgsConstructor
public class WithoutSaplController {

    private final ReactiveMongoUserRepository reactiveMongoUserRepository;

    public Flux<User> findAllByAgeAfter(int age) {
        return reactiveMongoUserRepository.findAllByAgeAfter(age);
    }

    public Flux<User> fetchingByQueryMethodLastnameContains(String lastnameContains) {
        return reactiveMongoUserRepository.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    public Flux<User> customRepositoryMethod() {
        return reactiveMongoUserRepository.customRepositoryMethod();
    }

    public Flux<User> findAllByAgeAfterAndRole(int age, String role) {
        return reactiveMongoUserRepository.findAllByAgeAfterAndRole(age, Objects.equals(role, "ADMIN") ? Role.ADMIN : Role.USER);
    }
}
