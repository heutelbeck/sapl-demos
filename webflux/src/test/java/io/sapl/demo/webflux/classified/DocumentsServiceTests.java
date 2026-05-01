package io.sapl.demo.webflux.classified;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import io.sapl.demo.webflux.testsupport.TestClock;
import io.sapl.demo.webflux.testsupport.TestClockConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestClockConfig.class)
class DocumentsServiceTests {

    @Autowired
    TestClock testClock;

    @Autowired
    DocumentsService documentsService;

    @Test
    @WithAnonymousUser
    void whenTimeZeroSecondOfMinute_thenAllRestricted() {
        testClock.setInstant(Instant.EPOCH);
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
        testClock.setInstant(Instant.EPOCH.plus(Duration.ofSeconds(25)));
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMatchesClearanceLevel(NatoSecurityClassification.COSMIC_TOP_SECRET))
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void whenTime45thSecondOfMinute_thenAllUnclassified() {
        testClock.setInstant(Instant.EPOCH.plus(Duration.ofSeconds(45)));
        StepVerifier.create(documentsService.getDocuments())
                .thenConsumeWhile(documentMatchesClearanceLevel(NatoSecurityClassification.NATO_UNCLASSIFIED))
                .verifyComplete();
    }

}
