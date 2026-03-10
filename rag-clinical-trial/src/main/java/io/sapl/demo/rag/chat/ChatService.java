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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String ERROR_GENERATION_FAILED = "An error occurred while generating the response. Please try again.";
    private static final int    TOP_K                   = 10;

    private final ChatClient chatClient;
    private final DocumentRetrievalService retrievalService;

    public Flux<String> askStreaming(String userMessage, String conversationHistory,
                                     Authentication authentication, boolean securityActive,
                                     Consumer<String> onStatus) {
        log.info("Received query: {}", userMessage);
        onStatus.accept("Retrieving documents");
        val searchRequest = SearchRequest.builder().query(userMessage).topK(TOP_K).build();
        return retrievalService.retrieve(Mono.just(searchRequest), securityActive)
                .doOnNext(docs -> {
                    log.info("Retrieved {} document chunks from vector store", docs.size());
                    onStatus.accept("Generating response");
                })
                .map(documents -> {
                    val context = buildContext(documents);
                    return buildPrompt(userMessage, context, conversationHistory);
                })
                .flatMapMany(prompt -> {
                    log.info("Sending prompt to LLM (streaming)");
                    return chatClient.prompt().user(prompt).stream().chatResponse()
                            .scan(new StreamState("", ""), ChatService::processResponse)
                            .skip(1)
                            .map(StreamState::content)
                            .filter(content -> !content.isEmpty());
                })
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                .doOnComplete(() -> log.info("LLM streaming response complete"))
                .onErrorResume(e -> {
                    if (isCancellation(e)) {
                        log.debug("Generation cancelled by user");
                        return Flux.empty();
                    }
                    log.error("Streaming failed", e);
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

    private static String buildContext(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "No relevant documents found.";
        }
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    private static String buildPrompt(String userMessage, String studyData, String conversationHistory) {
        if (conversationHistory == null || conversationHistory.isBlank()) {
            return """
                    Study data:
                    %s

                    %s""".formatted(studyData, userMessage);
        }
        return """
                Study data:
                %s

                Previous conversation:
                %s

                %s""".formatted(studyData, conversationHistory, userMessage);
    }

}
