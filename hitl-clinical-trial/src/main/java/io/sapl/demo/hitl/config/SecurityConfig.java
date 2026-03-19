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
package io.sapl.demo.hitl.config;

import io.sapl.demo.hitl.approval.ApprovalDeniedException;
import io.sapl.demo.hitl.approval.ApprovalTimeoutException;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import io.sapl.spring.config.EnableSaplMethodSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableSaplMethodSecurity
class SecurityConfig {

    @Bean
    ToolCallingManager toolCallingManager(ToolCallbackResolver toolCallbackResolver,
            ToolExecutionExceptionProcessor toolExecutionExceptionProcessor) {
        var defaultManager = ToolCallingManager.builder()
                .toolCallbackResolver(toolCallbackResolver)
                .toolExecutionExceptionProcessor(toolExecutionExceptionProcessor)
                .build();
        return new SecurityContextRestoringToolCallingManager(defaultManager);
    }

    @Bean
    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        var defaultProcessor = DefaultToolExecutionExceptionProcessor.builder().build();
        return exception -> {
            for (var cause = exception.getCause(); cause != null; cause = cause.getCause()) {
                if (cause instanceof ApprovalDeniedException || cause instanceof ApprovalTimeoutException) {
                    return cause.getMessage();
                }
            }
            return defaultProcessor.process(exception);
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

}
