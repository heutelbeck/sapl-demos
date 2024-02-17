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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
class SaplPdpBenchmarkIT {
    private static final String tmpReportPath = "target/tmp_benchmark_test";

    @BeforeAll
    private static void createEmptyBenchmarkResultFolder() throws IOException {
        var tmpReportPathFile = new File(tmpReportPath);
        FileUtils.deleteDirectory(tmpReportPathFile);
        //noinspection ResultOfMethodCallIgnored
        tmpReportPathFile.mkdir();
    }

    @Test
    void whenExecutingEmbeddedBenchmark_withNoAuth_thenReportsAreCreated() {
        var returnCode = new CommandLine(new BenchmarkCommand()).execute("--cfg", "src/test/resources/test_benchmark_config.yaml", "--output", tmpReportPath);
        Assertions.assertEquals(returnCode, 0);
        var reportFiles = List.of(
            "Report.html",
            "average_response.json",
            "custom.css",
            "favicon.png",
            "img/Decide Subscribe - Average Response Time.png",
            "img/Decide Subscribe - NoAuth - throughput.png",
            "img/EmbeddedBenchmark.NoAuthDecideSubscribe response time.png",
            "img/EmbeddedBenchmark.NoAuthDecideSubscribe throughput.png",
            "img/HttpBenchmark.NoAuthDecideSubscribe response time.png",
            "img/HttpBenchmark.NoAuthDecideSubscribe throughput.png",
            "img/RsocketBenchmark.NoAuthDecideSubscribe response time.png",
            "img/RsocketBenchmark.NoAuthDecideSubscribe throughput.png",
            "test_benchmark_config.yaml",
            "throughput_1threads.json"
        );
        for (String fileName: reportFiles) {
            File reportFile = new File(tmpReportPath+"/"+fileName);
            Assertions.assertTrue(reportFile.exists(), reportFile+" does not exist");
            Assertions.assertTrue(reportFile.length() >= 0, reportFile + " is empty");
        }
    }
}
