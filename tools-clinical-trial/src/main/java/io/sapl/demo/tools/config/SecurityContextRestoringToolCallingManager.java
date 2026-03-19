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
package io.sapl.demo.tools.config;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.model.tool.internal.ToolCallReactiveContextHolder;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;

/**
 * Wraps a {@link ToolCallingManager} to restore the {@link SecurityContext} from the
 * Reactor context before tool execution. Spring AI executes tools on a separate
 * boundedElastic thread where the SecurityContext ThreadLocal is empty. This wrapper
 * bridges the gap by reading the SecurityContext that was placed into the Reactor
 * subscriber context via {@code contextWrite} and setting it into
 * {@link SecurityContextHolder} before delegating tool execution.
 */
@RequiredArgsConstructor
class SecurityContextRestoringToolCallingManager implements ToolCallingManager {

    private final ToolCallingManager delegate;

    @Override
    public List<ToolDefinition> resolveToolDefinitions(ToolCallingChatOptions chatOptions) {
        return delegate.resolveToolDefinitions(chatOptions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ToolExecutionResult executeToolCalls(Prompt prompt, ChatResponse chatResponse) {
        var reactorCtx = ToolCallReactiveContextHolder.getContext();
        if (reactorCtx.hasKey(SecurityContext.class)) {
            var securityContextMono = (Mono<SecurityContext>) reactorCtx.get((Object) SecurityContext.class);
            var securityContext = securityContextMono.block();
            if (securityContext != null) {
                SecurityContextHolder.setContext(securityContext);
            }
        }
        try {
            return delegate.executeToolCalls(prompt, chatResponse);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

}
