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

import io.sapl.spring.method.metadata.PreEnforce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class DocumentRetrievalService {

    private final VectorStore vectorStore;

    @PreEnforce(action = "'retrieve'", environment = "{'securityActive': #securityActive}")
    Mono<List<Document>> retrieve(Mono<SearchRequest> searchRequest, boolean securityActive) {
        return searchRequest.flatMap(request -> {
            log.info("Filter expression reaching vectorStore: {}", request.getFilterExpression());
            return Mono.fromCallable(() -> vectorStore.similaritySearch(request))
                    .doOnNext(docs -> docs.forEach(d -> log.info("Retrieved doc metadata: {}", d.getMetadata())))
                    .subscribeOn(Schedulers.boundedElastic());
        });
    }

}
