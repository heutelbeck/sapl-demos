package io.sapl.demo.webflux.medical;

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

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestClockConfig.class)
class PatientsServiceTests {

    @Autowired
    PatientsService patientsController;

    @Autowired
    TestClock testClock;

    @Test
    @WithAnonymousUser
    void whenTimeZeroSecondOfMinute_thenIcdBalckenedAndDiagnosisRemoved() {
        testClock.setInstant(Instant.EPOCH);
        StepVerifier.create(patientsController.getPatients()).thenConsumeWhile(this::icdBlackenedAndDiagnosisRemoved)
                .verifyComplete();
    }

    private boolean icdBlackenedAndDiagnosisRemoved(Patient patient) {
        return patient.icd11Code().contains("█") && null == patient.diagnosis();
    }

    @Test
    @WithAnonymousUser
    void whenTime25thSecondOfMinute_thenIcdBlackenedWithStarsAndDiagnosisHidden() {
        testClock.setInstant(Instant.EPOCH.plus(Duration.ofSeconds(25)));
        StepVerifier.create(patientsController.getPatients())
                .thenConsumeWhile(this::icdBlackenedWithStarsAndDiagnosisHidden).verifyComplete();
    }

    private boolean icdBlackenedWithStarsAndDiagnosisHidden(Patient patient) {
        return patient.icd11Code().contains("*") && "[DIAGNOSIS HIDDEN]".equals(patient.diagnosis());
    }

    @Test
    @WithAnonymousUser
    void whenTime45thSecondOfMinute_thenIcdNotBlackenedAndPresent() {
        testClock.setInstant(Instant.EPOCH.plus(Duration.ofSeconds(45)));
        StepVerifier.create(patientsController.getPatients()).thenConsumeWhile(this::icdNotBlackenedAndPresent)
                .verifyComplete();
    }

    private boolean icdNotBlackenedAndPresent(Patient patient) {
        return !patient.icd11Code().contains("*") && !patient.icd11Code().contains("█")
                && !"[DIAGNOSIS HIDDEN]".equals(patient.diagnosis()) && null != patient.diagnosis();
    }
}
