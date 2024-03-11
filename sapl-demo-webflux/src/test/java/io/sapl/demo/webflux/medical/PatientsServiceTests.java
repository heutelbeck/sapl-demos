package io.sapl.demo.webflux.medical;

import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;

import reactor.test.StepVerifier;

@SpringBootTest
class PatientsServiceTests {

    @Autowired
    PatientsService patientsController;

    @MockBean
    Clock mockClock;

    @Test
    @WithAnonymousUser
    void whenTimeZeroSecondOfMinute_thenIcdBalckenedAndDiagnosisRemoved() {
        when(mockClock.instant()).thenReturn(Instant.EPOCH);
        StepVerifier.create(patientsController.getPatients()).thenConsumeWhile(this::icdBlackenedAndDiagnosisRemoved)
                .verifyComplete();
    }

    private boolean icdBlackenedAndDiagnosisRemoved(Patient patient) {
        return patient.icd11Code().contains("█") && patient.diagnosis() == null;
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
                && !"[DIAGNOSIS HIDDEN]".equals(patient.diagnosis()) && patient.diagnosis() != null;
    }
}
