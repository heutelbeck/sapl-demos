package io.sapl.demo.webflux.medical;

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

import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PatientsServiceTests {

    @Autowired
    PatientsService patientsController;

    @MockitoBean
    Clock mockClock;

    @Test
    @WithAnonymousUser
    void whenTimeZeroSecondOfMinute_thenIcdBalckenedAndDiagnosisRemoved() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH);
        StepVerifier.create(patientsController.getPatients()).thenConsumeWhile(this::icdBlackenedAndDiagnosisRemoved)
                .verifyComplete();
    }

    private boolean icdBlackenedAndDiagnosisRemoved(Patient patient) {
        return patient.icd11Code().contains("█") && null == patient.diagnosis();
    }

    @Test
    @WithAnonymousUser
    void whenTime25thSecondOfMinute_thenIcdBlackenedWithStarsAndDiagnosisHidden() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(25)));
        StepVerifier.create(patientsController.getPatients())
                .thenConsumeWhile(this::icdBlackenedWithStarsAndDiagnosisHidden).verifyComplete();
    }

    private boolean icdBlackenedWithStarsAndDiagnosisHidden(Patient patient) {
        return patient.icd11Code().contains("*") && "[DIAGNOSIS HIDDEN]".equals(patient.diagnosis());
    }

    @Test
    @WithAnonymousUser
    void whenTime45thSecondOfMinute_thenIcdNotBlackenedAndPresent() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofSeconds(45)));
        StepVerifier.create(patientsController.getPatients()).thenConsumeWhile(this::icdNotBlackenedAndPresent)
                .verifyComplete();
    }

    private boolean icdNotBlackenedAndPresent(Patient patient) {
        return !patient.icd11Code().contains("*") && !patient.icd11Code().contains("█")
                && !"[DIAGNOSIS HIDDEN]".equals(patient.diagnosis()) && null != patient.diagnosis();
    }
}
