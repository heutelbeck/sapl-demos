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
package io.sapl.demo.mcp.tools;

import io.sapl.spring.method.metadata.PreEnforce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicalTrialTools {

    private final CorpusLoader corpus;
    private final ToolCallStatus status;

    @PreEnforce(action = "'getStudyCatalog'")
    @Tool(description = "Returns a catalog of all available study datasets with their metadata (type, site, sensitivity level). Call this first to discover what data is available and which tools to use.")
    public String getStudyCatalog() {
        log.info("Tool executing: getStudyCatalog");
        status.update("Loading study catalog");
        val result = corpus.getStudyCatalog();
        log.info("Tool completed: getStudyCatalog ({} chars), sending result to LLM", result.length());
        status.update("Generating response");
        return result;
    }

    @PreEnforce(action = "'getStudyProtocol'")
    @Tool(description = "Retrieves the SMILE study protocol including study design, objectives, inclusion/exclusion criteria, endpoints, and methodology.")
    public String getStudyProtocol() {
        log.info("Tool executing: getStudyProtocol");
        status.update("Fetching study protocol");
        val result = corpus.getStudyProtocol();
        log.info("Tool completed: getStudyProtocol ({} chars), sending result to LLM", result.length());
        status.update("Generating response");
        return result;
    }

    @PreEnforce(action = "'getPhq9Assessments'", resource = "#site")
    @Tool(description = "Retrieves PHQ-9 depression assessment scores for a specific study site.")
    public String getPhq9Assessments(@ToolParam(description = "The study site to retrieve PHQ-9 data for. Use getStudyCatalog() to discover available sites.") String site) {
        log.info("Tool executing: getPhq9Assessments(site={})", site);
        status.update("Fetching PHQ-9 assessments");
        val result = corpus.getPhq9Assessments(site);
        log.info("Tool completed: getPhq9Assessments ({} chars), sending result to LLM", result.length());
        status.update("Generating response");
        return result;
    }

    @PreEnforce(action = "'getAdverseEventReports'", resource = "#site")
    @Tool(description = "Retrieves adverse event reports for a specific study site, including severity, causality, and resolution status.")
    public String getAdverseEventReports(@ToolParam(description = "The study site to retrieve adverse event reports for. Use getStudyCatalog() to discover available sites.") String site) {
        log.info("Tool executing: getAdverseEventReports(site={})", site);
        status.update("Fetching adverse event reports");
        val result = corpus.getAdverseEventReports(site);
        log.info("Tool completed: getAdverseEventReports ({} chars), sending result to LLM", result.length());
        status.update("Generating response");
        return result;
    }

    @PreEnforce(action = "'getParticipantRegistry'")
    @Tool(description = "Retrieves the participant registry containing real names, dates of birth, email addresses, and enrollment details. This is sensitive personal data.")
    public String getParticipantRegistry() {
        log.info("Tool executing: getParticipantRegistry");
        status.update("Fetching participant registry");
        val result = corpus.getParticipantRegistry();
        log.info("Tool completed: getParticipantRegistry ({} chars), sending result to LLM", result.length());
        status.update("Generating response");
        return result;
    }

}
