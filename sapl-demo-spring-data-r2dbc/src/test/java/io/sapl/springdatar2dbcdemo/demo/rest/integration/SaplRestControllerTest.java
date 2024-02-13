package io.sapl.springdatar2dbcdemo.demo.rest.integration;


import io.sapl.springdatar2dbcdemo.demo.repository.Person;
import io.sapl.springdatar2dbcdemo.demo.repository.Role;
import io.sapl.springdatar2dbcdemo.demo.rest.SaplRestController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import reactor.test.StepVerifier;

import java.util.List;


@SpringBootTest
class SaplRestControllerTest extends TestContainerBase {

    @Autowired
    SaplRestController saplRestController;
    
    @Test
    void when_findAllByAgeAfter_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new Person(4, "Te████", "Woodings", 96, null, Boolean.TRUE),
                new Person(6, "Ko█████████", "Hampton", 96, null, Boolean.FALSE),
                new Person(18, "Ka███", "Angell", 94, null, Boolean.FALSE),
                new Person(36, "Cl████████", "Courtois", 98, null, Boolean.FALSE),
                new Person(54, "He███████", "Enticknap", 94, null, Boolean.FALSE));

        // WHEN
        var result = saplRestController.findAllByAgeAfter(90).collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_customRepositoryMethod_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new Person(0, "Emerson", "Ro███", 82, Role.USER, Boolean.FALSE),
                new Person(0, "Terrel", "Wo██████", 96, Role.USER, Boolean.TRUE));
        // WHEN
        var result = saplRestController.customRepositoryMethod().collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_fetchingByQueryMethodLastnameContains_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new Person(16, "Tu████", "Morfell", 35, null, Boolean.TRUE),
                new Person(18, "Ka███", "Angell", 94, null, Boolean.FALSE),
                new Person(20, "Pe████", "Durtnall", 75, null, Boolean.FALSE),
                new Person(50, "Di████████", "Howgill", 69, null, Boolean.TRUE),
                new Person(53, "Ti███", "Gillbard", 38, null, Boolean.TRUE),
                new Person(76, "Gr██", "Ashall", 39, null, Boolean.TRUE),
                new Person(78, "Pr███", "Mosedill", 46, null, Boolean.TRUE),
                new Person(82, "Ca████", "Upstell", 85, null, Boolean.TRUE),
                new Person(90, "Da████", "Atwill", 28,null, Boolean.TRUE));

        // WHEN
        var result = saplRestController.fetchingByQueryMethodLastnameContains("ll").collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_findAllByAgeAfterAndFirstnameContaining_then_getDataWithoutAnyManipulation() {
        // GIVEN
        var expectedPersons = List.of(
                new Person(0, "Delinda", "Je██████████", 81, Role.ADMIN, Boolean.TRUE));

        // WHEN
        var result = saplRestController.findAllByAgeAfterAndActive(80, Boolean.TRUE).collectList();

        // THEN
        StepVerifier.create(result)
                .expectNext(expectedPersons)
                .verifyComplete();
    }

    @Test
    void when_findAllByAgeAfterAndActiveIsCalledButDecisionIsDenied_then_throwAccessDeniedException2() {
        // GIVEN

        // WHEN
        var accessDeniedException = saplRestController.findAllByAgeAfterAndActive(17, Boolean.FALSE).collectList();

        // THEN
        StepVerifier.create(accessDeniedException)
                .expectError(AccessDeniedException.class)
                .verify();
    }
}