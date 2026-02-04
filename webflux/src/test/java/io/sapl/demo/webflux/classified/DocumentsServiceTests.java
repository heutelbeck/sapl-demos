package io.sapl.demo.webflux.classified;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.function.Predicate;

import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DocumentsServiceTests {

    @MockitoBean
    Clock clock;

    @Autowired
    DocumentsService documentsService;

    @BeforeEach
    void setUp() {
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }

    @Test
    @WithAnonymousUser
    void whenTimeZeroSecondOfMinute_thenAllRestricted() {
        when(clock.instant()).thenReturn(Instant.EPOCH);
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMatchesClearanceLevel(NatoSecurityClassification.NATO_RESTRICTED))
                .verifyComplete();
    }

    private Predicate<Document> documentMatchesClearanceLevel(NatoSecurityClassification clearance) {
        return doc -> clearanceMatchesOrIsHigherThanClassification(clearance, doc.classification());
    }

    private boolean clearanceMatchesOrIsHigherThanClassification(NatoSecurityClassification clearance,
            NatoSecurityClassification classification) {
        return classification.compareTo(clearance) <= 0;
    }

    @Test
    @WithAnonymousUser
    void whenTime25thSecondOfMinute_thenAllTopSecret() {
        when(clock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(25)));
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMatchesClearanceLevel(NatoSecurityClassification.COSMIC_TOP_SECRET))
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void whenTime45thSecondOfMinute_thenAllUnclassified() {
        when(clock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(45)));
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMatchesClearanceLevel(NatoSecurityClassification.NATO_UNCLASSIFIED))
                .verifyComplete();
    }

}
