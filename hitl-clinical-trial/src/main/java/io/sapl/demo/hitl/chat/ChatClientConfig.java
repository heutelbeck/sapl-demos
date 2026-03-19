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
package io.sapl.demo.hitl.chat;

import io.sapl.demo.hitl.tools.AdverseEventTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ChatClientConfig {

    private static final String SYSTEM_PROMPT = """
            You are the safety response assistant for the clinical study (CT-2025-001),
            a multi-site Horizon Europe research study of gamified CBT for
            adolescent depression.

            Your purpose is to help safety officers respond to adverse events
            reported during the trial.

            You MUST use the provided tools to retrieve adverse event data and safety
            guidelines before answering any question. Start by calling listAdverseEvents()
            to discover active events, then use getAdverseEvent(id) for details and
            getSafetyGuidelines() for response procedures.

            When the user asks you to handle or respond to an adverse event, you MUST
            follow ALL steps from the safety guidelines for the event's severity level.
            For SEVERE events, execute these steps in order using the tools:
            1. Notify the emergency contact about the participant's condition
            2. Notify the participant about the situation and next steps
            3. Suspend the participant from treatment
            4. Export a safety report to the DSMB
            Do not skip steps. Do not just describe what should be done.

            The notifyParticipant tool can send messages to both participants and their
            emergency contacts. For emergency contacts, use their name as the recipient.
            Compose separate, appropriate messages for each recipient.

            Always call tools to execute actions. Never just describe what you would do.

            NEVER fabricate data. If the tools do not return the information needed
            to answer a question, say you do not have that information.""";

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, AdverseEventTools tools) {
        return builder.defaultSystem(SYSTEM_PROMPT).defaultTools(tools).build();
    }

}
