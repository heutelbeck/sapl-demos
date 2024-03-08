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

import io.sapl.benchmark.BenchmarkCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
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
        var returnCode = new CommandLine(new BenchmarkCommand()).execute("--cfg",
                "src/test/resources/test_benchmark_config.yaml", "--output", tmpReportPath);
        Assertions.assertEquals(0, returnCode);
        var reportFiles = List.of("Report.html", "average_response.json", "custom.css", "favicon.png",
                "img/Decide Subscribe - Average Response Time.png", "img/Decide Subscribe - noAuth - throughput.png",
                "img/EmbeddedBenchmark.noAuthDecideSubscribe response time.png",
                "img/EmbeddedBenchmark.noAuthDecideSubscribe throughput.png",
                "img/HttpBenchmark.noAuthDecideSubscribe response time.png",
                "img/HttpBenchmark.noAuthDecideSubscribe throughput.png",
                "img/RsocketBenchmark.noAuthDecideSubscribe response time.png",
                "img/RsocketBenchmark.noAuthDecideSubscribe throughput.png",
                "test_benchmark_config.yaml", "throughput_1threads.json");
        for (String fileName : reportFiles) {
            File reportFile = new File(tmpReportPath + "/" + fileName);
            assertTrue(reportFile.exists(), reportFile + " does not exist");
            assertTrue(reportFile.length() >= 0, reportFile + " is empty");

        }
    }
}
