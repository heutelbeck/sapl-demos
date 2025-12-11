package io.sapl.demo.webflux.classified;

import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.FilterPredicateConstraintHandlerProvider;

@Service
public class FilterClassifiedDocumentsContraintHandlerProvider implements FilterPredicateConstraintHandlerProvider {

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue objectValue)) {
            return false;
        }
        var type = objectValue.get("type");
        return type instanceof TextValue textValue && "filterClassifiedDocuments".equals(textValue.value());
    }

    @Override
    public Predicate<Object> getHandler(Value constraint) {
        var clearanceAux = NatoSecurityClassification.NATO_UNCLASSIFIED;

        if (constraint instanceof ObjectValue objectValue && objectValue.containsKey("clearance")) {
            var clearanceValue = objectValue.get("clearance");
            if (clearanceValue instanceof TextValue textValue) {
                try {
                    clearanceAux = NatoSecurityClassification.valueOf(textValue.value());
                } catch (IllegalArgumentException e) {
                    // NOOP
                }
            }
        }

        final var clearance = clearanceAux;

        return document -> clearanceMatchesOrIsHigherThanClassification(clearance,
                ((Document) document).classification());

    }

    private boolean clearanceMatchesOrIsHigherThanClassification(NatoSecurityClassification clearance,
            NatoSecurityClassification classification) {
        return classification.compareTo(clearance) <= 0;
    }

}
