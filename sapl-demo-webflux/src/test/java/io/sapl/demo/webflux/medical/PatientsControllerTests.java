package io.sapl.demo.webflux.medical;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;

@WebFluxTest(controllers = PatientsController.class, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class })
class PatientsControllerTests {

    @MockBean
    PatientsService patientsService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void whenGetPatients_thenPatientsServiceCalled() {
        final var patientZero = new Patient("name", "icd", "diag");
        final var patients    = Flux.just(patientZero);
        when(patientsService.getPatients()).thenReturn(patients);
        webTestClient.get().uri("/patients").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectBodyList(Patient.class).value(Function.identity(), hasItem(patientZero));
        verify(patientsService, times(1)).getPatients();
    }

}
