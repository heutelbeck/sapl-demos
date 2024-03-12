package io.sapl.demo.webflux.classified;

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

@WebFluxTest(controllers = DocumentsController.class, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class })
class DocumentsControllerTests {

    @MockBean
    DocumentsService documentsService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void whenGetPatients_thenPatientsServiceCalled() {
        var document  = new Document(NatoSecurityClassification.NATO_RESTRICTED, "name", "contents");
        var documents = Flux.just(document);
        when(documentsService.getDocuments()).thenReturn(documents);
        webTestClient.get().uri("/documents").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectBodyList(Document.class).value(Function.identity(), hasItem(document));
        verify(documentsService, times(1)).getDocuments();
    }

}
