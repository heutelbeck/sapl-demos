package io.sapl.demo.spring.handler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.ConsumerConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditTrailHandler implements ConsumerConstraintHandlerProvider<Object> {

    private final List<Map<String, Object>> auditLog = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Class<Object> getSupportedType() {
        return Object.class;
    }

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue obj)) {
            return false;
        }
        return obj.get("type") instanceof TextValue t && "auditTrail".equals(t.value());
    }

    @Override
    public Consumer<Object> getHandler(Value constraint) {
        var obj = (ObjectValue) constraint;
        var actionValue = obj.get("action");
        var action = actionValue instanceof TextValue t ? t.value() : "unknown";
        return value -> {
            var entry = new LinkedHashMap<String, Object>();
            entry.put("timestamp", Instant.now().toString());
            entry.put("action", action);
            entry.put("value", value);
            auditLog.add(entry);
            log.info("[AUDIT] {}: recorded response", action);
        };
    }

    public List<Map<String, Object>> getAuditLog() {
        return List.copyOf(auditLog);
    }

}
