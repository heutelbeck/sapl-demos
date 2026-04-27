package io.sapl.demo.spring.handler;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
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
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"enrichError".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(ErrorSignal.TYPE)) {
            return List.of();
        }
        var supportUrl = obj.get("supportUrl") instanceof TextValue(String url) ? url
                : "https://support.example.com";
        Mapper<Throwable> mapper = error -> {
            log.info("[ERROR-ENRICH] Enriching error with support URL: {}", supportUrl);
            var enriched = new RuntimeException(error.getMessage() + " | Support: " + supportUrl);
            enriched.initCause(error);
            return enriched;
        };
        return List.of(new ScopedConstraintHandler(mapper, ErrorSignal.TYPE, 50));
    }
}
