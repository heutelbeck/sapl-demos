package io.sapl.springdatar2dbcdemo.demo.controller;

import io.sapl.springdatar2dbcdemo.demo.repository.Person;
import io.sapl.springdatar2dbcdemo.demo.repository.unprotected.R2dbcPersonRepository;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;


@Controller
public class WithoutSaplController {

    private final R2dbcPersonRepository r2dbcPersonRepository;

    public WithoutSaplController(R2dbcPersonRepository r2dbcPersonRepository) {
        this.r2dbcPersonRepository = r2dbcPersonRepository;
    }

    public Flux<Person> findAllByAgeAfter(int age) {
        return r2dbcPersonRepository.findAllByAgeAfter(age);
    }

    public Flux<Person> fetchingByQueryMethodLastnameContains(String lastnameContains) {
        return r2dbcPersonRepository.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    public Flux<Person> customRepositoryMethod() {
        return r2dbcPersonRepository.customRepositoryMethod();
    }

    public Flux<Person> findAllByAgeAfterAndActive(int age, boolean active) {
        return r2dbcPersonRepository.findAllByAgeAfterAndActive(age, active);
    }

}
