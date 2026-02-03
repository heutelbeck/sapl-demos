package io.sapl.demo.webflux.classified;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = DocumentsController.class, excludeAutoConfiguration = {
        ReactiveWebSecurityAutoConfiguration.class })
class DocumentsControllerTests {

    @MockitoBean
    DocumentsService documentsService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void whenGetPatients_thenPatientsServiceCalled() {
        final var document  = new Document(NatoSecurityClassification.NATO_RESTRICTED, "name", "contents");
        final var documents = Flux.just(document);
        when(documentsService.getDocuments()).thenReturn(documents);
        webTestClient.get().uri("/documents").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectBodyList(Document.class).contains(document);
        verify(documentsService, times(1)).getDocuments();
    }

}
