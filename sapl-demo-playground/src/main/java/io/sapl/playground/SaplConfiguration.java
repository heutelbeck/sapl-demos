package io.sapl.playground;

import java.time.Clock;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.SchemaValidationLibrary;
import io.sapl.functions.StandardFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.interpreter.InitializationException;
import io.sapl.interpreter.combinators.CombiningAlgorithmFactory;
import io.sapl.interpreter.combinators.PolicyDocumentCombiningAlgorithm;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.pdp.config.PDPConfiguration;
import io.sapl.pdp.config.PDPConfigurationProvider;
import io.sapl.pip.TimePolicyInformationPoint;
import reactor.core.publisher.Flux;

@Configuration
public class SaplConfiguration {

    @Bean
    PDPConfigurationProvider pdpConfiguration() throws InitializationException {
        var attributeContext = new AnnotationAttributeContext();
        attributeContext.loadPolicyInformationPoint(new TimePolicyInformationPoint(Clock.systemUTC()));
        var functionContext = new AnnotationFunctionContext();
        functionContext.loadLibrary(FilterFunctionLibrary.class);
        functionContext.loadLibrary(StandardFunctionLibrary.class);
        functionContext.loadLibrary(TemporalFunctionLibrary.class);
        functionContext.loadLibrary(SchemaValidationLibrary.class);
        var staticPlaygroundConfiguration = new PDPConfiguration(attributeContext, functionContext, Map.of(),
                CombiningAlgorithmFactory.getCombiningAlgorithm(PolicyDocumentCombiningAlgorithm.DENY_OVERRIDES),
                UnaryOperator.identity(), UnaryOperator.identity());
        return new PDPConfigurationProvider() {
            @Override
            public Flux<PDPConfiguration> pdpConfiguration() {
                return Flux.just(staticPlaygroundConfiguration);
            }
        };
    }
}
