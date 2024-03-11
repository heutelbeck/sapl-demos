package io.sapl.demo.webflux.classified;

import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;

import reactor.test.StepVerifier;

@SpringBootTest
class DocumentsServiceTests {

    @Autowired
    DocumentsService documentsService;

    @MockBean
    Clock mockClock;

    @Test
    @WithAnonymousUser
    void whenTimeZeroSecondOfMinute_thenAllRestricted() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH);
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMactchesClearanceLevel(NatoSecurityClassification.NATO_RESTRICTED))
                .verifyComplete();
    }

    private Predicate<Document> documentMactchesClearanceLevel(NatoSecurityClassification clearance) {
        return doc -> clearanceMatchesOrIsHigherThanClassification(clearance, doc.classification());
    }

    private boolean clearanceMatchesOrIsHigherThanClassification(NatoSecurityClassification clearance,
            NatoSecurityClassification classification) {
        return classification.compareTo(clearance) <= 0;
    }

    @Test
    @WithAnonymousUser
    void whenTime25thSecondOfMinute_thenAllTopSecret() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(25)));
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMactchesClearanceLevel(NatoSecurityClassification.COSMIC_TOP_SECRET))
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void whenTime45thSecondOfMinute_thenAllUnclassified() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(45)));
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMactchesClearanceLevel(NatoSecurityClassification.NATO_UNCLASSIFIED))
                .verifyComplete();
    }

}
