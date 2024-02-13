package io.sapl.springdatar2dbcdemo.demo.rest.integration;

import io.sapl.springdatar2dbcdemo.demo.repository.Person;
import io.sapl.springdatar2dbcdemo.demo.repository.Role;
import io.sapl.springdatar2dbcdemo.demo.rest.WithoutSaplRestController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;


@SpringBootTest
class WithoutSaplRestControllerTest extends TestContainerBase {

    @Autowired
    WithoutSaplRestController withoutSaplRestController;

    @Test
    void when_findAllByAgeAfter_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new Person(4, "Terrel", "Woodings", 96, Role.USER, Boolean.TRUE),
                new Person(6, "Konstantine", "Hampton", 96, Role.USER, Boolean.FALSE),
                new Person(18, "Kacey", "Angell", 94, Role.USER, Boolean.FALSE),
                new Person(24, "Tabby", "Skittreal", 93, Role.ADMIN, Boolean.TRUE),
                new Person(36, "Claudianus", "Courtois", 98, Role.USER, Boolean.FALSE),
                new Person(46, "Lexine", "Blakden", 91, Role.ADMIN, Boolean.TRUE),
                new Person(47, "Petra", "Shackleford", 97, Role.ADMIN, Boolean.TRUE),
                new Person(54, "Henderson", "Enticknap", 94, Role.USER, Boolean.FALSE),
                new Person(69, "Deina", "Watting", 92, Role.ADMIN, Boolean.FALSE),
                new Person(73, "Jonie", "Delle", 95, Role.ADMIN, Boolean.FALSE),
                new Person(80, "Amy", "Gurnett", 99, Role.ADMIN, Boolean.TRUE),
                new Person(84, "Edi", "Giacopelo", 94, Role.ADMIN, Boolean.TRUE),
                new Person(93, "Jany", "Lambourne", 91, Role.ADMIN, Boolean.TRUE));

        // WHEN
        var result = withoutSaplRestController.findAllByAgeAfter(90).collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();

    }

    @Test
    void when_getAllAgeAfterAndActiveUsers_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new Person(4, "Terrel", "Woodings", 96, Role.USER, Boolean.TRUE),
                new Person(24, "Tabby", "Skittreal", 93, Role.ADMIN, Boolean.TRUE),
                new Person(46, "Lexine", "Blakden", 91, Role.ADMIN, Boolean.TRUE),
                new Person(47, "Petra", "Shackleford", 97, Role.ADMIN, Boolean.TRUE),
                new Person(80, "Amy", "Gurnett", 99, Role.ADMIN, Boolean.TRUE),
                new Person(84, "Edi", "Giacopelo", 94, Role.ADMIN, Boolean.TRUE),
                new Person(93, "Jany", "Lambourne", 91, Role.ADMIN, Boolean.TRUE));

        // WHEN
        var result = withoutSaplRestController.findAllByAgeAfterAndActive(90, Boolean.TRUE).collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_customRepositoryMethod_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(new Person(1, "Malinda", "Perrot", 53, Role.ADMIN, Boolean.TRUE),
                new Person(2, "Emerson", "Rowat", 82, Role.USER, Boolean.FALSE),
                new Person(3, "Yul", "Barukh", 79, Role.USER, Boolean.TRUE),
                new Person(4, "Terrel", "Woodings", 96, Role.USER, Boolean.TRUE),
                new Person(5, "Martino", "Bartolijn", 33, Role.USER, Boolean.FALSE),
                new Person(6, "Konstantine", "Hampton", 96, Role.USER, Boolean.FALSE),
                new Person(7, "Cathleen", "Simms", 25, Role.ADMIN, Boolean.FALSE),
                new Person(8, "Adolphe", "Streeton", 46, Role.USER, Boolean.TRUE),
                new Person(9, "Alessandro", "Tomaskov", 64, Role.USER, Boolean.TRUE),
                new Person(10, "Hobie", "Maddinon", 32, Role.USER, Boolean.FALSE),
                new Person(11, "Franni", "Mingey", 57, Role.ADMIN, Boolean.FALSE),
                new Person(12, "Giraldo", "Scade", 83, Role.USER, Boolean.TRUE),
                new Person(13, "Pooh", "Cocks", 19, Role.ADMIN, Boolean.TRUE),
                new Person(14, "Mario", "Albinson", 54, Role.USER, Boolean.FALSE),
                new Person(15, "Olav", "Hoopper", 31, Role.USER, Boolean.TRUE),
                new Person(16, "Tuckie", "Morfell", 35, Role.USER, Boolean.TRUE),
                new Person(17, "Sylas", "Bickerstasse", 66, Role.USER, Boolean.TRUE),
                new Person(18, "Kacey", "Angell", 94, Role.USER, Boolean.FALSE),
                new Person(19, "Dame", "Negri", 67, Role.USER, Boolean.TRUE));

        // WHEN
        var result = withoutSaplRestController.customRepositoryMethod().collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_findAllByFirstnameContaining_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new Person(4, "Terrel", "Woodings", 96, Role.USER, Boolean.TRUE),
                new Person(15, "Olav", "Hoopper", 31, Role.USER, Boolean.TRUE),
                new Person(81, "Lauraine", "Doogood", 54, Role.ADMIN, Boolean.FALSE));

        // WHEN
        var result = withoutSaplRestController.fetchingByQueryMethodLastnameContains("oo").collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }    
}