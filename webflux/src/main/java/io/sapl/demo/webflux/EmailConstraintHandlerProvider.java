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
package io.sapl.demo.webflux;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Consumer;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.OutputSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

/**
 * 4.1 ConstraintHandlerProvider implementation. Demonstrates a side-effect
 * obligation handler that runs after the protected method has produced a
 * result.
 */
@Slf4j
@Component
public class EmailConstraintHandlerProvider implements ConstraintHandlerProvider {

    private static final SignalType OUTPUT_OBJECT = OutputSignal.typeFor(Object.class);

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"sendEmail".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(OUTPUT_OBJECT)) {
            return List.of();
        }
        if (!(obj.get("recipient") instanceof TextValue(String recipient))) {
            return List.of();
        }
        if (!(obj.get("subject") instanceof TextValue(String subject))) {
            return List.of();
        }
        if (!(obj.get("message") instanceof TextValue(String message))) {
            return List.of();
        }
        Consumer<Object> handler = value -> sendEmail(recipient, subject, message);
        return List.of(new ScopedConstraintHandler(handler, OUTPUT_OBJECT, 50));
    }

    private static void sendEmail(String recipient, String subject, String message) {
        log.info("An E-Mail has been sent to {} with the subject '{}' and the message '{}'.", recipient, subject,
                message);
    }
}
