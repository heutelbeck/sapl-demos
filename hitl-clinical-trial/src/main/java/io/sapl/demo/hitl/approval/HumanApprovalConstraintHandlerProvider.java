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
package io.sapl.demo.hitl.approval;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import io.sapl.api.model.BooleanValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Runner;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.DecisionSignal;
import io.sapl.spring.pep.constraints.SignalType;
import io.sapl.spring.pep.constraints.providers.ConstraintResponsibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Constraint handler that blocks tool execution until a human approves or
 * denies the action. Triggered by SAPL obligations of type
 * {@code humanApprovalRequired}.
 * </p>
 * When the obligation is present on a PERMIT decision, this handler calls
 * {@link ApprovalService#requestApproval} which blocks the current thread
 * until the operator responds via the UI dialog. If the operator denies (or
 * the request times out), the handler throws {@link AccessDeniedException},
 * which causes the SAPL PEP to treat the obligation as failed and deny the
 * method invocation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HumanApprovalConstraintHandlerProvider implements ConstraintHandlerProvider {

    static final String ERROR_NO_SESSION_ID = "No session ID available for approval. Denying.";

    private static final String CONSTRAINT_TYPE  = "humanApprovalRequired";
    private static final int    DEFAULT_PRIORITY = 50;

    private final ApprovalService approvalService;

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!ConstraintResponsibility.isResponsible(constraint, CONSTRAINT_TYPE)) {
            return List.of();
        }
        if (!supportedSignals.contains(DecisionSignal.SIGNAL_TYPE)) {
            return List.of();
        }
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        Runner runner = buildRunner(obj);
        return List.of(new ScopedConstraintHandler(runner, DecisionSignal.SIGNAL_TYPE, DEFAULT_PRIORITY));
    }

    private Runner buildRunner(ObjectValue obj) {
        val toolName              = obj.get("toolName") instanceof TextValue(var name) ? name : "Action";
        val summary               = obj.get("summary") instanceof TextValue(var s) ? s : toolName;
        val detail                = obj.get("detail") instanceof TextValue(var d) ? d : toolName + " requires approval.";
        val forceHumanInteraction = obj.get("noAutoApprove") instanceof BooleanValue(var flag) && flag;
        val timeoutSeconds        = obj.get("timeout") instanceof TextValue(var iso)
                ? (int) Duration.parse(iso).toSeconds()
                : 0;
        return () -> {
            val sessionId = SessionIdHolder.get();
            if (sessionId == null) {
                log.warn(ERROR_NO_SESSION_ID);
                throw new AccessDeniedException(ERROR_NO_SESSION_ID);
            }
            val result = approvalService.requestApproval(sessionId, toolName, summary, detail, forceHumanInteraction,
                    timeoutSeconds);
            switch (result) {
            case APPROVED  -> { /* proceed */ }
            case DENIED    -> throw new ApprovalDeniedException(toolName, summary);
            case TIMED_OUT -> throw new ApprovalTimeoutException(toolName, summary, timeoutSeconds);
            }
        };
    }
}
