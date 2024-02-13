package io.sapl.springdatar2dbcdemo.demo.rest;

import io.sapl.springdatar2dbcdemo.demo.controller.SaplController;
import io.sapl.springdatar2dbcdemo.demo.repository.Person;
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
    public Flux<Person> findAllByAgeAfter(@PathVariable int age) {
        return saplController.findAllByAgeAfter(age);
    }

    @GetMapping("/user/fetchingByQueryMethodLastnameContains/{lastnameContains}")
    public Flux<Person> fetchingByQueryMethodLastnameContains(@PathVariable String lastnameContains) {
        return saplController.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    @GetMapping("/user/customRepositoryMethod")
    public Flux<Person> customRepositoryMethod() {
        return saplController.customRepositoryMethod();
    }

    @GetMapping("/user/findAllByAgeAfterAndActive/{age}/{active}")
    public Flux<Person> findAllByAgeAfterAndActive(@PathVariable int age, @PathVariable boolean active) {
        return saplController.findAllByAgeAfterAndActive(age, active);
    }
}
