package io.sapl.demo.webflux.medical;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = PatientsController.class, excludeAutoConfiguration = {
        ReactiveWebSecurityAutoConfiguration.class })
class PatientsControllerTests {

    @MockitoBean
    PatientsService patientsService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void whenGetPatients_thenPatientsServiceCalled() {
        final var patientZero = new Patient("name", "icd", "diag");
        final var patients    = Flux.just(patientZero);
        when(patientsService.getPatients()).thenReturn(patients);
        webTestClient.get().uri("/patients").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectBodyList(Patient.class).contains(patientZero);
        verify(patientsService, times(1)).getPatients();
    }

}
