package io.sapl.demo.spring.handler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Consumer;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditTrailHandler implements ConstraintHandlerProvider {

    private final List<Map<String, Object>> auditLog = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signalOpt = ConstraintHandlerProvider.constraintTypeAndAnyOutputSignal(constraint, "auditTrail",
                supportedSignals);
        if (signalOpt.isEmpty()) {
            return List.of();
        }
        var action = ConstraintHandlerProvider.stringField(constraint, "action").orElse("unknown");
        Consumer<Object> handler = value -> {
            var entry = new LinkedHashMap<String, Object>();
            entry.put("timestamp", Instant.now().toString());
            entry.put("action", action);
            entry.put("value", value);
            auditLog.add(entry);
            log.info("[AUDIT] {}: recorded response", action);
        };
        return List.of(new ScopedConstraintHandler(handler, signalOpt.get(), 50));
    }

    public List<Map<String, Object>> getAuditLog() {
        return List.copyOf(auditLog);
    }
}
