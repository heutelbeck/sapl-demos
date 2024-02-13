package io.sapl.springdatamongoreactivedemo.demo.rest;

import io.sapl.springdatamongoreactivedemo.demo.controller.SaplController;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
public class SaplRestController {

    private final SaplController saplController;

    @GetMapping("/user/findAllByAgeAfter/{age}")
    public Flux<User> findAllByAgeAfter(@PathVariable int age) {
        return saplController.findAllByAgeAfter(age);
    }

    @GetMapping("/user/fetchingByQueryMethodLastnameContains/{lastnameContains}")
    public Flux<User> fetchingByQueryMethodLastnameContains(@PathVariable String lastnameContains) {
        return saplController.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    @GetMapping("/user/customRepositoryMethod")
    public Flux<User> customRepositoryMethod() {
        return saplController.customRepositoryMethod();
    }

    @GetMapping("/user/findAllByAgeAfterAndRole/{age}/{role}")
    public Flux<User> findAllByAgeAfterAndRole(@PathVariable int age, @PathVariable String role) {
        return saplController.findAllByAgeAfterAndRole(age, role);
    }
}
