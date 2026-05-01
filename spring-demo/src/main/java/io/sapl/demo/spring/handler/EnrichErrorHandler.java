package io.sapl.demo.spring.handler;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.ErrorSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class EnrichErrorHandler implements ConstraintHandlerProvider {

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signalOpt = ConstraintHandlerProvider.constraintTypeAndSignal(constraint, "enrichError", supportedSignals,
                ErrorSignal.SIGNAL_TYPE);
        if (signalOpt.isEmpty()) {
            return List.of();
        }
        var supportUrl = ConstraintHandlerProvider.stringField(constraint, "supportUrl")
                .orElse("https://support.example.com");
        Mapper<Throwable> mapper = error -> {
            log.info("[ERROR-ENRICH] Enriching error with support URL: {}", supportUrl);
            var enriched = new RuntimeException(error.getMessage() + " | Support: " + supportUrl);
            enriched.initCause(error);
            return enriched;
        };
        return List.of(new ScopedConstraintHandler(mapper, signalOpt.get(), 50));
    }
}
