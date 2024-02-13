package io.sapl.springdatamongoreactivedemo.demo.controller;

import io.sapl.springdatamongoreactivedemo.demo.repository.Role;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import io.sapl.springdatamongoreactivedemo.demo.repository.sapl.ProtectedReactiveMongoUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Objects;


@Controller
@AllArgsConstructor
public class SaplController {

    private ProtectedReactiveMongoUserRepository protectedReactiveMongoUserRepository;

    public Flux<User> findAllByAgeAfter(int age) {
        return protectedReactiveMongoUserRepository.findAllByAgeAfter(age);
    }

    public Flux<User> fetchingByQueryMethodLastnameContains(String lastnameContains) {
        return protectedReactiveMongoUserRepository.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    public Flux<User> customRepositoryMethod() {
        return protectedReactiveMongoUserRepository.customRepositoryMethod();
    }

    public Flux<User> findAllByAgeAfterAndRole(int age, String role) {
        return protectedReactiveMongoUserRepository.findAllByAgeAfterAndRole(age, Objects.equals(role, "ADMIN") ? Role.ADMIN : Role.USER);
    }
}
