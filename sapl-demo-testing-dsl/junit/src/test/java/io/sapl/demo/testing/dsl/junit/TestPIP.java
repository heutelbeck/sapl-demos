/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.demo.testing.dsl.junit;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Text;
import java.util.Map;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = TestPIP.NAME, description = TestPIP.DESCRIPTION)
public class TestPIP {

    public static final String NAME = "test";

    public static final String DESCRIPTION = "Policy information Point for testing";

    @Attribute
    public Flux<Val> upper(@Text Val leftHandValue, Map<String, Val> variables) {
        return Flux.just(Val.of(leftHandValue.get().asText().toUpperCase()));
    }

    @Attribute
    public Flux<Val> hasEnvVar(@Text Val leftHandValue, Map<String, Val> variables) {
        return Flux.just(variables.getOrDefault(leftHandValue.get().asText(), Val.of("something")));
    }

    @Attribute
    public Flux<Val> hasAuthzSubVar(@Text Val leftHandValue, Map<String, Val> variables) {
        final var env = variables.get("environment");
        if(env.isUndefined()) {
            return Flux.just(Val.of("no environment"));
        }
        return Flux.just(Val.of(env.getObjectNode().get(leftHandValue.getText()).asText("something else")));
    }
}
