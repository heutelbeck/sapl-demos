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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
class CorpusLoader {

    private static final String SITE_HEIDELBERG = "heidelberg";
    private static final String SITE_EDINBURGH  = "edinburgh";

    @Getter
    private final String studyProtocol;
    @Getter
    private final String participantRegistry;

    private final Map<String, String> phq9BySite;
    private final Map<String, String> adverseEventsBySite;

    CorpusLoader() {
        studyProtocol = loadCorpusFile("study_protocol.md");
        participantRegistry = loadCorpusFile("participant_registry.md");
        phq9BySite = Map.of(
                SITE_HEIDELBERG, loadCorpusFile("site_heidelberg_phq9.md"),
                SITE_EDINBURGH, loadCorpusFile("site_edinburgh_phq9.md"));

        val allAdverseEvents = loadCorpusFile("adverse_events.md");
        adverseEventsBySite = Map.of(
                SITE_HEIDELBERG, filterAdverseEventsBySite(allAdverseEvents, "Heidelberg"),
                SITE_EDINBURGH, filterAdverseEventsBySite(allAdverseEvents, "Edinburgh"));

        log.info("Loaded 5 corpus files into memory (adverse events split by site)");
    }

    String getPhq9Assessments(String site) {
        return phq9BySite.getOrDefault(normalizeSite(site), "Unknown site: " + site);
    }

    String getAdverseEventReports(String site) {
        return adverseEventsBySite.getOrDefault(normalizeSite(site), "Unknown site: " + site);
    }

    String getStudyCatalog() {
        return """
                Available study datasets:

                1. study_protocol (sensitivity: low, site: all)
                   Study design, objectives, inclusion/exclusion criteria, endpoints, and methodology.
                   Tool: getStudyProtocol()

                2. phq9_heidelberg (sensitivity: high, site: heidelberg)
                   PHQ-9 depression assessment scores for Heidelberg site participants.
                   Tool: getPhq9Assessments(site="heidelberg")

                3. phq9_edinburgh (sensitivity: high, site: edinburgh)
                   PHQ-9 depression assessment scores for Edinburgh site participants.
                   Tool: getPhq9Assessments(site="edinburgh")

                4. adverse_events_heidelberg (sensitivity: high, site: heidelberg)
                   Adverse event reports from the Heidelberg site.
                   Tool: getAdverseEventReports(site="heidelberg")

                5. adverse_events_edinburgh (sensitivity: high, site: edinburgh)
                   Adverse event reports from the Edinburgh site.
                   Tool: getAdverseEventReports(site="edinburgh")

                6. participant_registry (sensitivity: critical, site: all)
                   Participant real names, dates of birth, email addresses, and enrollment details.
                   Tool: getParticipantRegistry()""";
    }

    private static String normalizeSite(String site) {
        if (site == null || site.isBlank()) {
            return "";
        }
        return site.strip().toLowerCase();
    }

    private static String filterAdverseEventsBySite(String markdown, String siteName) {
        val lines = markdown.lines().collect(Collectors.toList());
        val filtered = new StringBuilder();
        filtered.append("# Adverse Event Reports - ").append(siteName).append(" Site\n\n");
        for (val line : lines) {
            if (line.startsWith("|") && (line.contains("Alias") || line.contains("---") || line.contains(siteName))) {
                filtered.append(line).append('\n');
            }
            if (line.startsWith("## Notes") || line.startsWith("All adverse")) {
                filtered.append('\n').append(line).append('\n');
            }
        }
        return filtered.toString();
    }

    private static String loadCorpusFile(String filename) {
        val path = "corpus/" + filename;
        try (val stream = CorpusLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Corpus file not found: " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
