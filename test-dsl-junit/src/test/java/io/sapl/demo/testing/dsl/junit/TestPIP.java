/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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

import io.sapl.api.attributes.Attribute;
import io.sapl.api.attributes.PolicyInformationPoint;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.UndefinedValue;
import io.sapl.api.model.Value;
import reactor.core.publisher.Flux;

import java.util.Map;

@PolicyInformationPoint(name = TestPIP.NAME, description = TestPIP.DESCRIPTION)
public class TestPIP {

    public static final String NAME = "test";

    public static final String DESCRIPTION = "Policy information Point for testing";

    @Attribute
    public Flux<Value> upper(TextValue leftHandValue, Map<String, Value> variables) {
        return Flux.just(Value.of(leftHandValue.value().toUpperCase()));
    }

    @Attribute
    public Flux<Value> hasEnvVar(TextValue leftHandValue, Map<String, Value> variables) {
        return Flux.just(variables.getOrDefault(leftHandValue.value(), Value.of("something")));
    }

    @Attribute
    public Flux<Value> hasAuthzSubVar(TextValue leftHandValue, Map<String, Value> variables) {
        final var env = variables.get("environment");
        if (env instanceof UndefinedValue) {
            return Flux.just(Value.of("no environment"));
        }
        if (!(env instanceof ObjectValue objectValue)) {
            return Flux.just(Value.of("no object"));
        }
        var v = objectValue.get(leftHandValue.value());
        return Flux.just(Value.of(v==null?"something else":v.toString()));
    }
}
