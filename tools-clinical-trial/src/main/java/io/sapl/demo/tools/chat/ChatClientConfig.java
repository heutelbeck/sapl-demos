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
package io.sapl.demo.tools.chat;

import io.sapl.demo.tools.tools.ClinicalTrialTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ChatClientConfig {

    private static final String SYSTEM_PROMPT = """
            You are the AI assistant for the clinical study (CT-2025-001),
            a multi-site Horizon Europe research study of gamified CBT for
            adolescent depression.

            Your purpose is to assist researchers in data analytics and correlation.

            You MUST use the provided tools to retrieve study data before answering
            any question. Start by calling getStudyCatalog() to discover what datasets
            are available and which sites exist. Then call the appropriate tools to
            fetch the data you need.

            Call MULTIPLE tools when a question requires cross-referencing data.
            For example, to find a specific participant's PHQ-9 score, call
            getParticipantRegistry to identify the participant and their site,
            then call getPhq9Assessments with the correct site to retrieve scores,
            and correlate the results.

            NEVER fabricate data or scores. If the tools do not return the information
            needed to answer a question, say you do not have that information.""";

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ClinicalTrialTools tools) {
        return builder.defaultSystem(SYSTEM_PROMPT).defaultTools(tools).build();
    }

}
