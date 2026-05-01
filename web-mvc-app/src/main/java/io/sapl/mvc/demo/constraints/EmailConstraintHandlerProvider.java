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
package io.sapl.mvc.demo.constraints;

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
 * Custom side-effect constraint handler. Fires a {@link Runner} at the
 * {@link DecisionSignal} for any decision carrying a well-formed
 * {@code sendEmail} obligation/advice; the runner sends an email (here,
 * logged for demo purposes).
 * </p>
 * Migrated from the legacy {@code RunnableConstraintHandlerProvider} to the
 * unified {@link ConstraintHandlerProvider} interface.
 */
@Slf4j
@Component
public class EmailConstraintHandlerProvider implements ConstraintHandlerProvider {

    private static final String CONSTRAINT_TYPE  = "sendEmail";
    private static final int    DEFAULT_PRIORITY = 50;

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signalOpt = ConstraintHandlerProvider.constraintTypeAndSignal(constraint, CONSTRAINT_TYPE,
                supportedSignals, DecisionSignal.SIGNAL_TYPE);
        if (signalOpt.isEmpty()) {
            return List.of();
        }
        var fieldsOpt = ConstraintHandlerProvider.requiredStringFields(constraint, "recipient", "subject", "message");
        if (fieldsOpt.isEmpty()) {
            return List.of();
        }
        var fields = fieldsOpt.get();
        Runner runner = () -> sendEmail(fields.get("recipient"), fields.get("subject"), fields.get("message"));
        return List.of(new ScopedConstraintHandler(runner, signalOpt.get(), DEFAULT_PRIORITY));
    }

    private static void sendEmail(String recipient, String subject, String message) {
        log.info("An E-Mail has been sent to {} with the subject '{}' and the message '{}'.", recipient, subject,
                message);
    }
}
