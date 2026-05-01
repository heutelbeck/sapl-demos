package io.sapl.demo.webflux.classified;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.SignalType;
import reactor.core.publisher.Flux;

/**
 * Whole-publisher mapper bound to {@code OutputSignal<Flux<Document>>}.
 * Returns a {@link Flux} filtered to the documents the requesting
 * subject's clearance is permitted to read.
 */
@Service
public class FilterClassifiedDocumentsContraintHandlerProvider implements ConstraintHandlerProvider {

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signalOpt = ConstraintHandlerProvider.constraintTypeAndOutputSignal(constraint,
                "filterClassifiedDocuments", supportedSignals, Flux.class, Document.class);
        if (signalOpt.isEmpty()) {
            return List.of();
        }
        var clearance = ConstraintHandlerProvider.stringField(constraint, "clearance")
                .map(FilterClassifiedDocumentsContraintHandlerProvider::parseClearance)
                .orElse(NatoSecurityClassification.NATO_UNCLASSIFIED);
        Mapper<Flux<Document>> mapper = flux -> flux.filter(document -> matches(clearance, document.classification()));
        return List.of(new ScopedConstraintHandler(mapper, signalOpt.get(), 50));
    }

    private static NatoSecurityClassification parseClearance(String value) {
        try {
            return NatoSecurityClassification.valueOf(value);
        } catch (IllegalArgumentException e) {
            return NatoSecurityClassification.NATO_UNCLASSIFIED;
        }
    }

    private static boolean matches(NatoSecurityClassification clearance, NatoSecurityClassification classification) {
        return classification.compareTo(clearance) <= 0;
    }
}
