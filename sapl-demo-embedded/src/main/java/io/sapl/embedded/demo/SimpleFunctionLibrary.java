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
package io.sapl.embedded.demo;

import io.sapl.api.functions.Function;
import io.sapl.api.functions.FunctionLibrary;
import io.sapl.api.model.ArrayValue;
import io.sapl.api.model.NumberValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;

@FunctionLibrary(name = "simple", description = "some simple functions")
public class SimpleFunctionLibrary {

    private SimpleFunctionLibrary() {
        // Utility class with only static methods. Cannot instantiate.
    }

    @Function
    public static Value length(Value parameter) {
        return switch (parameter) {
            case ArrayValue array -> Value.of(array.size());
            case TextValue text ->  Value.of(text.value().length());
            default -> Value.error("length() parameter must be a string or an array, found: %s.".formatted(parameter));
        };
    }

    @Function
    public static Value append(Value... parameters) {
        final var builder = new StringBuilder();
        for (var parameter : parameters) {
            switch(parameter) {
                case TextValue text -> builder.append(text.value());
                case NumberValue number -> builder.append(number.value());
                default -> {/*NOOP*/}
            }
        }
        return Value.of(builder.toString());
    }

}
