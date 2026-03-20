package io.sapl.demo.spring.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.UnaryOperator;

import io.sapl.api.model.ArrayValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.MappingConstraintHandlerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
class RedactFieldsHandler implements MappingConstraintHandlerProvider<Object> {

    private final ObjectMapper mapper;

    @Override
    public Class<Object> getSupportedType() {
        return Object.class;
    }

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue obj)) {
            return false;
        }
        return obj.get("type") instanceof TextValue t && "redactFields".equals(t.value());
    }

    @Override
    @SuppressWarnings("unchecked")
    public UnaryOperator<Object> getHandler(Value constraint) {
        var obj = (ObjectValue) constraint;
        var fieldsValue = obj.get("fields");
        List<String> fields = new ArrayList<>();
        if (fieldsValue instanceof ArrayValue arr) {
            for (var element : arr) {
                if (element instanceof TextValue t) {
                    fields.add(t.value());
                }
            }
        }
        return value -> {
            LinkedHashMap<String, Object> map = mapper.convertValue(value, LinkedHashMap.class);
            for (var field : fields) {
                if (map.containsKey(field)) {
                    log.info("[REDACT] Redacting field: {}", field);
                    map.put(field, "[REDACTED]");
                }
            }
            return map;
        };
    }

}
