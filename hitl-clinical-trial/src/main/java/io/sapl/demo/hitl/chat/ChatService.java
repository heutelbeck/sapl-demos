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

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String ERROR_GENERATION_FAILED = "An error occurred while generating the response. Please try again.";

    private final ChatClient chatClient;

    public Flux<String> askStreaming(String userMessage, String conversationHistory, Consumer<String> onStatus) {
        log.info("Received query: {}", userMessage);
        val prompt = buildPrompt(userMessage, conversationHistory);
        onStatus.accept("Thinking");

        return chatClient.prompt().user(prompt).stream().chatResponse()
                .scan(new StreamState("", ""), ChatService::processResponse)
                .skip(1)
                .map(StreamState::content)
                .filter(content -> !content.isEmpty())
                .doOnComplete(() -> log.info("LLM streaming response complete"))
                .onErrorResume(e -> {
                    if (isCancellation(e)) {
                        log.debug("Generation cancelled by user");
                        return Flux.empty();
                    }
                    log.error("Generation failed", e);
                    return Flux.just(ERROR_GENERATION_FAILED);
                });
    }

    private static boolean isCancellation(Throwable e) {
        for (var cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof InterruptedException) {
                return true;
            }
        }
        return false;
    }

    private record StreamState(String lastId, String content) {}

    private static StreamState processResponse(StreamState previous, ChatResponse response) {
        val id = response.getMetadata().getId();
        val results = response.getResults();
        val text = (results != null && !results.isEmpty()) ? results.getFirst().getOutput().getText() : null;
        val content = text != null ? text : "";

        if (id != null && !id.isEmpty() && !id.equals(previous.lastId()) && !previous.lastId().isEmpty()) {
            log.info("Tool-calling round boundary: [{}] -> [{}]", previous.lastId(), id);
            return new StreamState(id, "\n\n" + content);
        }

        val effectiveId = (id != null && !id.isEmpty()) ? id : previous.lastId();
        return new StreamState(effectiveId, content);
    }

    private static String buildPrompt(String userMessage, String conversationHistory) {
        if (conversationHistory == null || conversationHistory.isBlank()) {
            return userMessage;
        }
        return """
                Previous conversation:
                %s

                %s""".formatted(conversationHistory, userMessage);
    }

}
