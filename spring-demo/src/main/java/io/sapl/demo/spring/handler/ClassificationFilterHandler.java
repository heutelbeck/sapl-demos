package io.sapl.demo.spring.handler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

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
class ClassificationFilterHandler implements ConstraintHandlerProvider {

    private static final Map<String, Integer> CLASSIFICATION_LEVELS = Map.of(
            "PUBLIC", 0,
            "INTERNAL", 1,
            "CONFIDENTIAL", 2,
            "SECRET", 3);

    private final ObjectMapper mapper;

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signalOpt = ConstraintHandlerProvider.constraintTypeAndAnyOutputSignal(constraint, "filterByClassification",
                supportedSignals);
        if (signalOpt.isEmpty()) {
            return List.of();
        }
        var maxLevel = ConstraintHandlerProvider.stringField(constraint, "maxLevel").orElse("PUBLIC");
        var maxRank  = CLASSIFICATION_LEVELS.getOrDefault(maxLevel, 0);
        Mapper<Object> handler = value -> {
            if (value instanceof List<?> list) {
                return list.stream().filter(element -> isAllowed(element, maxLevel, maxRank)).toList();
            }
            return isAllowed(value, maxLevel, maxRank) ? value : null;
        };
        return List.of(new ScopedConstraintHandler(handler, signalOpt.get(), 50));
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
