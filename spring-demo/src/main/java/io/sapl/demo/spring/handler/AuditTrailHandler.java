package io.sapl.demo.spring.handler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Consumer;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.OutputSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditTrailHandler implements ConstraintHandlerProvider {

    private static final SignalType OUTPUT_OBJECT = OutputSignal.typeFor(Object.class);

    private final List<Map<String, Object>> auditLog = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"auditTrail".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(OUTPUT_OBJECT)) {
            return List.of();
        }
        var action = obj.get("action") instanceof TextValue(String a) ? a : "unknown";
        Consumer<Object> handler = value -> {
            var entry = new LinkedHashMap<String, Object>();
            entry.put("timestamp", Instant.now().toString());
            entry.put("action", action);
            entry.put("value", value);
            auditLog.add(entry);
            log.info("[AUDIT] {}: recorded response", action);
        };
        return List.of(new ScopedConstraintHandler(handler, OUTPUT_OBJECT, 50));
    }

    public List<Map<String, Object>> getAuditLog() {
        return List.copyOf(auditLog);
    }
}
