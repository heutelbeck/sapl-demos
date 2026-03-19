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
package io.sapl.demo.rag.ingest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
class DocumentIngestionService {

    private static final String ERROR_READING_CORPUS = "Failed to read corpus files from classpath";
    private static final String WARN_NO_DOCUMENTS    = "No documents found in corpus directory";

    private static final String SENSITIVITY = "sensitivity";

    private static final Map<String, Map<String, String>> METADATA_BY_FILE = Map.of(
            "study_protocol",       Map.of("type", "protocol", "site", "all", SENSITIVITY, "low"),
            "site_heidelberg_phq9", Map.of("type", "phq9", "site", "heidelberg", SENSITIVITY, "high"),
            "site_edinburgh_phq9",  Map.of("type", "phq9", "site", "edinburgh", SENSITIVITY, "high"),
            "adverse_events",       Map.of("type", "adverse_event", "site", "all", SENSITIVITY, "high"),
            "participant_registry", Map.of("type", "registry", "site", "all", SENSITIVITY, "critical")
    );

    private final VectorStore vectorStore;

    @EventListener(ApplicationReadyEvent.class)
    void ingestDocuments() {
        if (alreadyIngested()) {
            log.info("Documents already present in vector store. Skipping ingestion.");
            return;
        }

        val allDocuments = loadAndChunkDocuments();
        if (allDocuments.isEmpty()) {
            log.warn(WARN_NO_DOCUMENTS);
            return;
        }

        vectorStore.add(allDocuments);
        log.info("Ingested {} document chunks into vector store.", allDocuments.size());
    }

    private boolean alreadyIngested() {
        val results = vectorStore.similaritySearch(
                SearchRequest.builder().query("clinical trial").topK(1).build());
        return !results.isEmpty();
    }

    private List<Document> loadAndChunkDocuments() {
        val allDocuments = new ArrayList<Document>();
        val resolver     = new PathMatchingResourcePatternResolver();

        Resource[] resources;
        try {
            resources = resolver.getResources("classpath:corpus/*.md");
        } catch (IOException e) {
            log.error(ERROR_READING_CORPUS, e);
            return allDocuments;
        }

        for (val resource : resources) {
            val filename = resource.getFilename();
            if (filename == null) {
                continue;
            }

            val fileKey  = filename.replace(".md", "");
            val metadata = METADATA_BY_FILE.getOrDefault(fileKey, Map.of());

            val config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(false)
                    .withIncludeCodeBlock(true)
                    .withIncludeBlockquote(true)
                    .build();

            val reader    = new MarkdownDocumentReader(resource, config);
            val documents = reader.get();

            for (val document : documents) {
                val enrichedMetadata = new HashMap<>(document.getMetadata());
                enrichedMetadata.putAll(metadata);
                allDocuments.add(new Document(document.getText(), enrichedMetadata));
            }

            log.info("Loaded {} chunks from {}", documents.size(), filename);
        }

        return allDocuments;
    }

}
