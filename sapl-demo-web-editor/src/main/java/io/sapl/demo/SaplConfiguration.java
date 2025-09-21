package io.sapl.demo;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import io.sapl.attributes.documentation.api.PolicyInformationPointDocumentationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.interpreter.Val;
import io.sapl.attributes.broker.impl.AnnotationPolicyInformationPointLoader;
import io.sapl.attributes.broker.impl.CachingAttributeStreamBroker;
import io.sapl.attributes.broker.impl.InMemoryPolicyInformationPointDocumentationProvider;
import io.sapl.attributes.pips.time.TimePolicyInformationPoint;
import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.SchemaValidationLibrary;
import io.sapl.functions.StandardFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.interpreter.InitializationException;
import io.sapl.interpreter.combinators.PolicyDocumentCombiningAlgorithm;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.pdp.config.PDPConfiguration;
import io.sapl.pdp.config.PDPConfigurationProvider;
import io.sapl.prp.Document;
import io.sapl.prp.PolicyRetrievalPoint;
import io.sapl.prp.PolicyRetrievalResult;
import io.sapl.validation.ValidatorFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class SaplConfiguration {

    @Bean
    PolicyInformationPointDocumentationProvider docsProvider() {
        return new InMemoryPolicyInformationPointDocumentationProvider();
    }

    @Bean
    PDPConfigurationProvider pdpConfiguration(PolicyInformationPointDocumentationProvider docsProvider) throws InitializationException, JsonProcessingException {
        final var mapper                = new ObjectMapper();
        final var validatorFactory      = new ValidatorFactory(mapper);
        final var attributeStreamBroker = new CachingAttributeStreamBroker();
        final var pipLoader             = new AnnotationPolicyInformationPointLoader(attributeStreamBroker,
                docsProvider, validatorFactory);
        pipLoader.loadPolicyInformationPoint(new TimePolicyInformationPoint(Clock.systemUTC()));
        pipLoader.loadStaticPolicyInformationPoint(DemoPip.class);
        final var functionContext = new AnnotationFunctionContext();
        functionContext.loadLibrary(FilterFunctionLibrary.class);
        functionContext.loadLibrary(StandardFunctionLibrary.class);
        functionContext.loadLibrary(TemporalFunctionLibrary.class);
        functionContext.loadLibrary(SchemaValidationLibrary.class);
        functionContext.loadLibrary(DemoLib.class);
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

        final var staticPlaygroundConfiguration = new PDPConfiguration("demoConfig", attributeStreamBroker,
                functionContext, Map.of("abba", Val.ofJson("""
                        {
                            "a": {
                                "x": 0,
                                "y": 1
                            },
                            "b": "y"
                        }
                        """)), PolicyDocumentCombiningAlgorithm.DENY_OVERRIDES, UnaryOperator.identity(),
                UnaryOperator.identity(), dummyPrp);

        return () -> Flux.just(staticPlaygroundConfiguration);
    }
}
