package io.sapl.playground;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.SchemaValidationLibrary;
import io.sapl.functions.StandardFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.interpreter.InitializationException;
import io.sapl.interpreter.combinators.PolicyDocumentCombiningAlgorithm;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.pdp.config.PDPConfiguration;
import io.sapl.pdp.config.PDPConfigurationProvider;
import io.sapl.pip.TimePolicyInformationPoint;
import io.sapl.prp.Document;
import io.sapl.prp.PolicyRetrievalPoint;
import io.sapl.prp.PolicyRetrievalResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class SaplConfiguration {

    @Bean
    PDPConfigurationProvider pdpConfiguration() throws InitializationException {
        final var attributeContext = new AnnotationAttributeContext();
        attributeContext.loadPolicyInformationPoint(new TimePolicyInformationPoint(Clock.systemUTC()));
        final var functionContext = new AnnotationFunctionContext();
        functionContext.loadLibrary(FilterFunctionLibrary.class);
        functionContext.loadLibrary(StandardFunctionLibrary.class);
        functionContext.loadLibrary(TemporalFunctionLibrary.class);
        functionContext.loadLibrary(SchemaValidationLibrary.class);
        final var dummyPrp = new PolicyRetrievalPoint() {

            @Override
            public Mono<PolicyRetrievalResult> retrievePolicies() {
                return Mono.empty();
            }

            @Override
            public Collection<Document> allDocuments() {
                return List.of();
            }

            @Override
            public boolean isConsistent() {
                return true;
            }

        };

        final var staticPlaygroundConfiguration = new PDPConfiguration("demoConfig", attributeContext, functionContext,
                Map.of(), PolicyDocumentCombiningAlgorithm.DENY_OVERRIDES, UnaryOperator.identity(),
                UnaryOperator.identity(), dummyPrp);

        return () -> Flux.just(staticPlaygroundConfiguration);
    }
}
