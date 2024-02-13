package io.sapl.springdatamongoreactivedemo.demo.rest.integration;

import io.sapl.springdatamongoreactivedemo.demo.repository.Role;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import io.sapl.springdatamongoreactivedemo.demo.rest.SaplRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import reactor.test.StepVerifier;

import java.util.List;

import static io.sapl.springdatamongoreactivedemo.demo.repository.Role.USER;

@SpringBootTest
class SaplRestControllerTest {

    @Autowired
    SaplRestController saplRestController;
    
    @Test
    void when_findAllByAgeAfter_then_getDataWithManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new User(null, "Terrel", "Wo██████", 96, Role.USER, Boolean.TRUE),
                new User(null, "Konstantine", "Ha█████", 96, Role.USER, Boolean.TRUE),
                new User(null, "Kacey", "An████", 94, Role.USER, Boolean.FALSE),
                new User(null, "Claudianus", "Co██████", 98, Role.USER, Boolean.TRUE),
                new User(null, "Lexine", "Bl█████", 92, Role.USER, Boolean.TRUE),
                new User(null, "Henderson", "En███████", 94, Role.USER, Boolean.FALSE));

        // WHEN
        var result = saplRestController.findAllByAgeAfter(90).collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_customRepositoryMethod_then_getDataWithManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new User(null, "Terrel", "Wo██████", 96, Role.USER, Boolean.TRUE),
                new User(null, "Konstantine", "Ha█████", 96, Role.USER, Boolean.FALSE));

        // WHEN
        var result = saplRestController.customRepositoryMethod().collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_fetchingByQueryMethodLastnameContains_then_getDataWithManipulation() {
        // GIVEN
        var expectedUsers = List.of(
                new User(null, "Tuckie", "Mo█████", 35, Role.USER, Boolean.TRUE),
                new User(null, "Kacey", "An████", 94, Role.USER, Boolean.FALSE),
                new User(null, "Perren", "Du██████", 75, Role.USER, Boolean.FALSE),
                new User(null, "Dianemarie", "Ho█████", 69, Role.USER, Boolean.TRUE),
                new User(null, "Titus", "Gi██████", 38, Role.USER, Boolean.TRUE),
                new User(null, "Gray", "As████", 39, Role.USER, Boolean.TRUE),
                new User(null, "Pryce", "Mo██████", 46, Role.USER, Boolean.TRUE),
                new User(null, "Casper", "Up█████", 85, Role.USER, Boolean.TRUE),
                new User(null, "Darwin", "At████", 28, USER, Boolean.TRUE));

        // WHEN
        var result = saplRestController.fetchingByQueryMethodLastnameContains("ll").collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedUsers)
                .verifyComplete();
    }

    @Test
    void when_findAllByAgeAfterAndFirstnameContaining_then_getDataWithManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new User(null, "Terrel", "Wo██████", 96, Role.USER, Boolean.TRUE),
                new User(null, "Konstantine", "Ha█████", 96, Role.USER, Boolean.TRUE),
                new User(null, "Claudianus", "Co██████", 98, Role.USER, Boolean.TRUE),
                new User(null, "Lexine", "Bl█████", 92, Role.USER, Boolean.TRUE));

        // WHEN
        var result = saplRestController.findAllByAgeAfterAndRole(90, "USER").collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }
    
    @Test
    void when_findAllByAgeAfterAndFirstnameContaining_then_throwAccessDeniedException() {
        // WHEN
        var accessDeniedException = saplRestController.findAllByAgeAfterAndRole(17, "USER").collectList();

        // THEN
        StepVerifier.create(accessDeniedException)
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void when_findAllByAgeAfterAndFirstnameContainingIsCalledButDecisionIsDenied_then_throwAccessDeniedException() {
        // GIVEN

        // WHEN
        var accessDeniedException = saplRestController.findAllByAgeAfterAndRole(90, "ADMIN").collectList();

        // THEN
        StepVerifier.create(accessDeniedException)
                .expectError(AccessDeniedException.class)
                .verify();
    }
}