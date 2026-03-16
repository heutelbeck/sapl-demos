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

import io.sapl.api.model.BooleanValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.RunnableConstraintHandlerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Constraint handler that blocks tool execution until a human approves or
 * denies the action. Triggered by SAPL obligations of type
 * {@code humanApprovalRequired}.
 *
 * <p>When the obligation is present on a PERMIT decision, this handler calls
 * {@link ApprovalService#requestApproval} which blocks the current thread
 * until the operator responds via the UI dialog. If the operator denies (or
 * the request times out), the handler throws {@link AccessDeniedException},
 * which causes the SAPL PEP to treat the obligation as failed and deny the
 * method invocation.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HumanApprovalConstraintHandlerProvider implements RunnableConstraintHandlerProvider {

    static final String ERROR_ACTION_DENIED = "Action denied by operator.";
    static final String ERROR_NO_SESSION_ID = "No session ID available for approval. Denying.";

    private final ApprovalService approvalService;

    @Override
    public Signal getSignal() {
        return Signal.ON_DECISION;
    }

    @Override
    public boolean isResponsible(Value constraint) {
        return constraint instanceof ObjectValue obj
                && obj.get("type") instanceof TextValue type
                && "humanApprovalRequired".equals(type.value());
    }

    @Override
    public Runnable getHandler(Value constraint) {
        val obj = (ObjectValue) constraint;
        val toolName = obj.get("toolName") instanceof TextValue(var name) ? name : "Action";
        val forceHumanInteraction = obj.get("noAutoApprove") instanceof BooleanValue(var flag) && flag;
        val timeoutSeconds = obj.get("timeout") instanceof TextValue(var iso)
                ? (int) Duration.parse(iso).toSeconds()
                : 0;
        return () -> {
            val sessionId = SessionIdHolder.get();
            if (sessionId == null) {
                log.warn(ERROR_NO_SESSION_ID);
                throw new AccessDeniedException(ERROR_NO_SESSION_ID);
            }
            val approved = approvalService.requestApproval(sessionId, toolName, toolName,
                    toolName + " requires approval.", forceHumanInteraction, timeoutSeconds);
            if (!approved) {
                throw new AccessDeniedException(ERROR_ACTION_DENIED);
            }
        };
    }

}
