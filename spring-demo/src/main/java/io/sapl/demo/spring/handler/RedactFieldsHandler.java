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
import io.sapl.spring.pep.constraints.Signal.OutputSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
class RedactFieldsHandler implements ConstraintHandlerProvider {

    private static final SignalType OUTPUT_OBJECT = OutputSignal.typeFor(Object.class);

    private final ObjectMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"redactFields".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(OUTPUT_OBJECT)) {
            return List.of();
        }
        List<String> fields = new ArrayList<>();
        if (obj.get("fields") instanceof ArrayValue arr) {
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
        return List.of(new ScopedConstraintHandler(handler, OUTPUT_OBJECT, 50));
    }
}
