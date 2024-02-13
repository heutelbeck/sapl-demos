package io.sapl.springdatamongoreactivedemo.demo.rest;

import io.sapl.springdatamongoreactivedemo.demo.controller.WithoutSaplController;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
public class WithoutSaplRestController {

    private final WithoutSaplController withoutSaplController;

    @GetMapping("/admin/findAllByAgeAfter/{age}")
    public Flux<User> findAllByAgeAfter(@PathVariable int age) {
        return withoutSaplController.findAllByAgeAfter(age);
    }

    @GetMapping("/admin/fetchingByQueryMethodLastnameContains/{lastnameContains}")
    public Flux<User> fetchingByQueryMethodLastnameContains(@PathVariable String lastnameContains) {
        return withoutSaplController.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    @GetMapping("/admin/customRepositoryMethod")
    public Flux<User> customRepositoryMethod() {
        return withoutSaplController.customRepositoryMethod();
    }

    @GetMapping("/admin/findAllByAgeAfterAndRole/{age}/{role}")
    public Flux<User> findAllByAgeAfterAndRole(@PathVariable int age, @PathVariable String role) {
        return withoutSaplController.findAllByAgeAfterAndRole(age, role);
    }
}

