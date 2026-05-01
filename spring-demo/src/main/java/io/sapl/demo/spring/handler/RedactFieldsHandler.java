package io.sapl.demo.spring.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.ArrayValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
class RedactFieldsHandler implements ConstraintHandlerProvider {

    private final ObjectMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signalOpt = ConstraintHandlerProvider.constraintTypeAndAnyOutputSignal(constraint, "redactFields",
                supportedSignals);
        if (signalOpt.isEmpty()) {
            return List.of();
        }
        // The "fields" attribute is an array, not a single string, so use the typed
        // accessor directly rather than the stringField helper.
        List<String> fields = new ArrayList<>();
        if (constraint instanceof ObjectValue obj && obj.get("fields") instanceof ArrayValue arr) {
            for (var element : arr) {
                if (element instanceof TextValue(String text)) {
                    fields.add(text);
                }
            }
        }
        Mapper<Object> handler = value -> {
            LinkedHashMap<String, Object> map = mapper.convertValue(value, LinkedHashMap.class);
            for (var field : fields) {
                if (map.containsKey(field)) {
                    log.info("[REDACT] Redacting field: {}", field);
                    map.put(field, "[REDACTED]");
                }
            }
            return map;
        };
        return List.of(new ScopedConstraintHandler(handler, signalOpt.get(), 50));
    }
}
