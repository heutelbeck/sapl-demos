package io.sapl.demo.spring.handler;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

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
class ClassificationFilterHandler implements MappingConstraintHandlerProvider<Object> {

    private static final Map<String, Integer> CLASSIFICATION_LEVELS = Map.of(
            "PUBLIC", 0,
            "INTERNAL", 1,
            "CONFIDENTIAL", 2,
            "SECRET", 3
    );

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
        return obj.get("type") instanceof TextValue t && "filterByClassification".equals(t.value());
    }

    @Override
    public UnaryOperator<Object> getHandler(Value constraint) {
        var obj = (ObjectValue) constraint;
        var maxLevelValue = obj.get("maxLevel");
        var maxLevel = maxLevelValue instanceof TextValue t ? t.value() : "PUBLIC";
        var maxRank = CLASSIFICATION_LEVELS.getOrDefault(maxLevel, 0);
        return value -> {
            if (value instanceof List<?> list) {
                return list.stream().filter(element -> isAllowed(element, maxLevel, maxRank)).toList();
            }
            return isAllowed(value, maxLevel, maxRank) ? value : null;
        };
    }

    private boolean isAllowed(Object element, String maxLevel, int maxRank) {
        var classification = extractClassification(element);
        if (classification == null) {
            log.warn("[FILTER] Element excluded: no classification");
            return false;
        }
        var rank = CLASSIFICATION_LEVELS.get(classification);
        if (rank == null) {
            log.warn("[FILTER] Element excluded: unknown classification {}", classification);
            return false;
        }
        var allowed = rank <= maxRank;
        if (!allowed) {
            log.info("[FILTER] Excluded {} element (max: {})", classification, maxLevel);
        }
        return allowed;
    }

    private String extractClassification(Object element) {
        Map<?, ?> map = mapper.convertValue(element, Map.class);
        return map.get("classification") instanceof String s ? s : null;
    }

}
