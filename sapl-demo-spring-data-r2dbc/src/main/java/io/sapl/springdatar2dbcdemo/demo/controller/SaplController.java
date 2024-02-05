package io.sapl.springdatar2dbcdemo.demo.controller;

import io.sapl.springdatar2dbcdemo.demo.repository.Person;
import io.sapl.springdatar2dbcdemo.demo.repository.sapl.ProtectedR2dbcPersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;


@Controller
@AllArgsConstructor
public class SaplController {

    private final ProtectedR2dbcPersonRepository protectedR2dbcPersonRepository;

    public Flux<Person> findAllByAgeAfter(int age) {
        return protectedR2dbcPersonRepository.findAllByAgeAfter(age);
    }

    public Flux<Person> fetchingByQueryMethodLastnameContains(String lastnameContains) {
        return protectedR2dbcPersonRepository.fetchingByQueryMethodLastnameContains(lastnameContains);
    }

    public Flux<Person> customRepositoryMethod() {
        return protectedR2dbcPersonRepository.customRepositoryMethod();
    }

    public Flux<Person> findAllByAgeAfterAndActive(int age, boolean active) {
        return protectedR2dbcPersonRepository.findAllByAgeAfterAndActive(age, active);
    }

}
