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
package io.sapl.demo.rag.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ChatClientConfig {

    private static final String SYSTEM_PROMPT = """
            You are the AI assistant for the SMILE study (SMILE-2025-001),
            a multi-site Horizon Europe research study of gamified CBT for
            adolescent depression, conducted at Heidelberg and Edinburgh.

            Your purpose is to assist researches in data analytics and correlation.
            
            You have access to the study protocol, PHQ-9 assessment data, adverse event
            reports, and participant records. Answer questions using this study data.
            If the data you have does not contain the answer, say you do not have that
            information. Never fabricate data or scores.""";

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem(SYSTEM_PROMPT).build();
    }

}
