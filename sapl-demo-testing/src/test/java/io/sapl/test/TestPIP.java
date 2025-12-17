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
package io.sapl.test;

import java.util.Map;

import io.sapl.api.attributes.Attribute;
import io.sapl.api.attributes.PolicyInformationPoint;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import reactor.core.publisher.Flux;

@PolicyInformationPoint(name = TestPIP.NAME, description = TestPIP.DESCRIPTION)
public class TestPIP {

    public static final String NAME = "test";

    public static final String DESCRIPTION = "Policy information Point for testing";

    @Attribute
    public Flux<Value> upper(TextValue leftHandValue, Map<String, Value> variables) {
        return Flux.just(Value.of(leftHandValue.value().toUpperCase()));
    }

}
