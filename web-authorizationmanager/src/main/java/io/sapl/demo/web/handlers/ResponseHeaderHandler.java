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
package io.sapl.demo.web.handlers;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal;
import io.sapl.spring.pep.constraints.SignalType;
import io.sapl.spring.pep.constraints.providers.ConstraintResponsibility;
import io.sapl.spring.pep.http.MutableHttpResponse;
import lombok.val;

/**
 * Claims the {@code response-header} obligation and attaches a Consumer to
 * {@link Signal.HttpResponseSignal} that adds the named header to the
 * outbound response. Obligation shape: {@code {"type": "response-header",
 * "name": "X-Foo", "value": "bar"}}.
 */
@Component
public class ResponseHeaderHandler implements ConstraintHandlerProvider {

    private static final String CONSTRAINT_TYPE = "response-header";

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!ConstraintResponsibility.isResponsible(constraint, CONSTRAINT_TYPE)) {
            return List.of();
        }
        if (!supportedSignals.contains(Signal.HttpResponseSignal.TYPE)) {
            return List.of();
        }
        if (!(constraint instanceof ObjectValue object) || !(object.get("name") instanceof TextValue(String name))
                || !(object.get("value") instanceof TextValue(String value))) {
            return List.of();
        }
        val capturedName  = name;
        val capturedValue = value;
        ConstraintHandler.Consumer<MutableHttpResponse> handler = response -> response.setHeader(capturedName,
                capturedValue);
        return List.of(new ScopedConstraintHandler(handler, Signal.HttpResponseSignal.TYPE, 0));
    }
}
