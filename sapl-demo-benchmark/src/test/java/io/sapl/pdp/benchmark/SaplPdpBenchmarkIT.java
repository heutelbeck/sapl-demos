/*
 * Copyright (C) 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.pdp.benchmark;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.sapl.benchmark.BenchmarkCommand;
import picocli.CommandLine;

class SaplPdpBenchmarkIT {
    private static final String tmpReportPath = "results/tmp_benchmark_test";

    @BeforeAll
    private static void createEmptyBenchmarkResultFolder() throws IOException {
        var tmpReportPathFile = new File(tmpReportPath);
        FileUtils.deleteDirectory(tmpReportPathFile);
        assertTrue(tmpReportPathFile.mkdirs());
    }

    @Test
    void whenExecutingEmbeddedBenchmark_withNoAuth_thenReportsAreCreated() {
        // start benchmark
        var returnCode = new CommandLine(new BenchmarkCommand()).execute("--cfg",
                "src/test/resources/test_benchmark_config.yaml", "--output", tmpReportPath);
        Assertions.assertEquals(0, returnCode);

        // build a list of expected report files
        List<String> reportFiles = new ArrayList<>(List.of("Report.html", "average_response.json", "custom.css",
                "favicon.png", "test_benchmark_config.yaml", "throughput_1threads.json"));
        for (var decisionMethod: List.of("Decide Subscribe", "Decide Once")) {
            reportFiles.add("img/" + decisionMethod + " - Average Response Time.png");
            for (var authMethod: List.of("noAuth", "basicAuth", "apiKey", "oAuth2")) {
                reportFiles.add("img/" + decisionMethod + " - " + authMethod + " - throughput.png");
                for (var benchmarkType: List.of("EmbeddedBenchmark", "HttpBenchmark", "RsocketBenchmark")) {
                    // embedded supports only noAuth
                    if ( !benchmarkType.equals("EmbeddedBenchmark") || authMethod.equals("noAuth") ) {
                        reportFiles.add("img/" + benchmarkType + "." + authMethod +
                                decisionMethod.replace(" ", "") + " response time.png");
                        reportFiles.add("img/" + benchmarkType + "." + authMethod +
                                decisionMethod.replace(" ", "") + " throughput.png");
                    }
                }
            }
        }

        // ensure that all expected report files are present and not empty
        for (String fileName : reportFiles) {
            File reportFile = new File(tmpReportPath + "/" + fileName);
            assertTrue(reportFile.exists(), reportFile + " does not exist");
            assertTrue(reportFile.length() >= 0, reportFile + " is empty");

        }
    }
}
