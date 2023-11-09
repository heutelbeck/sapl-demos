package io.sapl.demo;

import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.grammar.sapl.CombiningAlgorithm;
import io.sapl.pdp.config.VariablesAndCombinatorSource;
import reactor.core.publisher.Flux;

@Configuration
public class SaplConfiguration {

    /**
     * The Editor requires access to variables for looking up schema definitions.
     * 
     * @return a dummy VariablesAndCombinatorSource 
     */
    @Bean
    VariablesAndCombinatorSource variablesAndCombinatorSource() {
        return new VariablesAndCombinatorSource() {

            @Override
            public Flux<Optional<CombiningAlgorithm>> getCombiningAlgorithm() {
                return Flux.empty();
            }

            @Override
            public Flux<Optional<Map<String, JsonNode>>> getVariables() {
                return Flux.empty();
            }

        };
    }
}
