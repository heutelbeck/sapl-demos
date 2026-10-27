/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.demo.vaadin.security;

import static io.sapl.spring.pep.constraints.ConstraintHandlerProvider.constraintTypeAndSignal;
import static io.sapl.spring.pep.constraints.ConstraintHandlerProvider.stringField;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Runner;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.DecisionSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

/**
 * Logs the message of a constraint of type {@code log} when the decision
 * arrives, for example
 *
 * <pre>
 * advice { "type": "log", "message": "Pizza form was visited outside opening hours." }
 * </pre>
 */
@Slf4j
@Component
public class LoggingConstraintHandlerProvider implements ConstraintHandlerProvider {

    private static final String CONSTRAINT_TYPE = "log";
    private static final String FIELD_MESSAGE   = "message";

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signal = constraintTypeAndSignal(constraint, CONSTRAINT_TYPE, supportedSignals, DecisionSignal.SIGNAL_TYPE);
        if (signal.isEmpty()) {
            return List.of();
        }
        var    message = stringField(constraint, FIELD_MESSAGE).orElse("");
        Runner logging = () -> log.info(message);
        return List.of(new ScopedConstraintHandler(logging, signal.get(), 0));
    }

}
