package io.sapl.demo.webflux.classified;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.OutputSignal;
import io.sapl.spring.pep.constraints.SignalType;

/**
 * 4.1 OutputSignal mapper. Returns the document if the user's clearance
 * matches or exceeds its classification, otherwise drops it (returns null).
 * Effective only when the streaming PEPs are operational; the scaffold
 * implementation in the current release does not run the plan.
 */
@Service
public class FilterClassifiedDocumentsContraintHandlerProvider implements ConstraintHandlerProvider {

    private static final SignalType OUTPUT_DOCUMENT = OutputSignal.typeFor(Document.class);

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"filterClassifiedDocuments".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(OUTPUT_DOCUMENT)) {
            return List.of();
        }
        var clearance = parseClearance(obj);
        Mapper<Document> mapper = document -> matches(clearance, document.classification()) ? document : null;
        return List.of(new ScopedConstraintHandler(mapper, OUTPUT_DOCUMENT, 50));
    }

    private static NatoSecurityClassification parseClearance(ObjectValue obj) {
        if (obj.get("clearance") instanceof TextValue(String value)) {
            try {
                return NatoSecurityClassification.valueOf(value);
            } catch (IllegalArgumentException e) {
                return NatoSecurityClassification.NATO_UNCLASSIFIED;
            }
        }
        return NatoSecurityClassification.NATO_UNCLASSIFIED;
    }

    private static boolean matches(NatoSecurityClassification clearance, NatoSecurityClassification classification) {
        return classification.compareTo(clearance) <= 0;
    }
}
