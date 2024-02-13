package io.sapl.springdatar2dbcdemo.demo.rest;

import io.sapl.springdatar2dbcdemo.demo.controller.WithoutSaplController;
import io.sapl.springdatar2dbcdemo.demo.repository.Person;
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
    public Flux<Person> findAllByAgeAfter(@PathVariable int age) {
        return withoutSaplController.findAllByAgeAfter(age);
    }

    @GetMapping("/admin/fetchingByQueryMethodLastnameContains/{lastnameContains}")
    public Flux<Person> fetchingByQueryMethodLastnameContains(@PathVariable String lastnameContains) {
        return withoutSaplController.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    @GetMapping("/admin/customRepositoryMethod")
    public Flux<Person> customRepositoryMethod() {
        return withoutSaplController.customRepositoryMethod();
    }

    @GetMapping("/admin/findAllByAgeAfterAndActive/{age}/{active}")
    public Flux<Person> findAllByAgeAfterAndActive(@PathVariable int age, @PathVariable boolean active) {
        return withoutSaplController.findAllByAgeAfterAndActive(age, active);
    }
}

