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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;

import io.sapl.benchmark.BenchmarkConfiguration;
import io.sapl.benchmark.BenchmarkExecutionContext;
import io.sapl.benchmark.jmh.EmbeddedBenchmark;
import io.sapl.benchmark.jmh.Helper;
import io.sapl.benchmark.jmh.HttpBenchmark;
import io.sapl.benchmark.jmh.RsocketBenchmark;
import io.sapl.interpreter.InitializationException;

class SaplPdpBenchmarkTest {
    private static final String tmpReportPath = "tmp_benchmark_test";

    @BeforeAll
    private static void createEmptyBenchmarkFolder() throws IOException {
        var tmpReportPathFile = new File(tmpReportPath);
        FileUtils.deleteDirectory(tmpReportPathFile);

        // noinspection ResultOfMethodCallIgnored
        tmpReportPathFile.mkdir();
    }

    @Test
    void whenExecutingEmbeddedBenchmark_withValidSubscription_thenDecisionIsAccepted()
            throws InitializationException, IOException {
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.runHttpBenchmarks    = false;
        benchmarkConfig.runRsocketBenchmarks = false;
        benchmarkConfig
                .setSubscription("{\"subject\": \"Willi\", \"action\": \"requests\", \"resource\": \"information\"}");
        var embeddedBenchmark = new EmbeddedBenchmark();
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            var benchmarkContext = benchmarkConfig.getBenchmarkExecutionContext(null, null);
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            embeddedBenchmark.setup();
            embeddedBenchmark.noAuthDecideOnce();
            embeddedBenchmark.noAuthDecideSubscribe();
        }
    }

    @Test
    void whenExecutingEmbeddedBenchmark_withInvalidSubscription_thenExceptionIsThrown()
            throws InitializationException, IOException {
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.runHttpBenchmarks    = false;
        benchmarkConfig.runRsocketBenchmarks = false;
        benchmarkConfig.setSubscription(
                "{\"subject\": \"Willi\", \"action\": \"invalid action\", \"resource\": \"information\"}");
        var embeddedBenchmark = new EmbeddedBenchmark();
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            var benchmarkContext = benchmarkConfig.getBenchmarkExecutionContext(null, null);
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            embeddedBenchmark.setup();
            assertThrows(RuntimeException.class, embeddedBenchmark::noAuthDecideOnce);
            assertThrows(RuntimeException.class, embeddedBenchmark::noAuthDecideSubscribe);
        }
    }

    @Test
    void whenLoadingContaxtFromString_withInvalidJson_thenExcpetionIsThrown() {
        assertThrows(RuntimeException.class, () -> BenchmarkExecutionContext.fromString("{invalidjson]"));
    }

    @Test
    void whenExecutingHttpBenchmark_thenDecisionIsAccepted() throws IOException {
        var mockedContainer = Mockito.mock(GenericContainer.class);
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.runHttpBenchmarks    = false;
        benchmarkConfig.runRsocketBenchmarks = false;
        benchmarkConfig.useBasicAuth         = true;
        benchmarkConfig.basicClientKey       = "123";
        benchmarkConfig.basicClientSecret    = "123";
        benchmarkConfig.useAuthApiKey        = true;
        benchmarkConfig.apiKeyHeader         = "API_KEY";
        benchmarkConfig.apiKey               = "123";
        benchmarkConfig.useOauth2            = true;
        var benchmark = new HttpBenchmark();
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            var benchmarkContext = benchmarkConfig.getBenchmarkExecutionContext(mockedContainer, mockedContainer);
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            benchmark.setup();
            try (MockedStatic<Helper> mockedHelper = Mockito.mockStatic(Helper.class)) {
                mockedHelper.when(() -> Helper.decide(any(), any())).then(__ -> null);
                mockedHelper.when(() -> Helper.decideOnce(any(), any())).then(__ -> null);
                // NoAuth
                benchmark.noAuthDecideOnce();
                benchmark.noAuthDecideSubscribe();
                // BasicAuth
                benchmark.basicAuthDecideOnce();
                benchmark.basicAuthDecideSubscribe();
                // ApiKey
                benchmark.apiKeyDecideOnce();
                benchmark.apiKeyDecideSubscribe();
                // Oauth2
                benchmark.oAuth2DecideOnce();
                benchmark.oAuth2DecideSubscribe();
            }
        }
    }

    @Test
    void whenExecutingRsocketBenchmark_thenDecisionIsAccepted() throws IOException {
        var mockedContainer = Mockito.mock(GenericContainer.class);
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.runHttpBenchmarks    = false;
        benchmarkConfig.runRsocketBenchmarks = false;
        benchmarkConfig.useBasicAuth         = true;
        benchmarkConfig.basicClientKey       = "123";
        benchmarkConfig.basicClientSecret    = "123";
        benchmarkConfig.useAuthApiKey        = true;
        benchmarkConfig.apiKeyHeader         = "API_KEY";
        benchmarkConfig.apiKey               = "123";
        var benchmark = new RsocketBenchmark();
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            var benchmarkContext = benchmarkConfig.getBenchmarkExecutionContext(mockedContainer, mockedContainer);
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            benchmark.setup();
            try (MockedStatic<Helper> mockedHelper = Mockito.mockStatic(Helper.class)) {
                mockedHelper.when(() -> Helper.decide(any(), any())).then(__ -> null);
                mockedHelper.when(() -> Helper.decideOnce(any(), any())).then(__ -> null);
                // NoAuth
                benchmark.noAuthDecideOnce();
                benchmark.noAuthDecideSubscribe();
                // BasicAuth
                benchmark.basicAuthDecideOnce();
                benchmark.basicAuthDecideSubscribe();
                // ApiKey
                benchmark.apiKeyDecideOnce();
                benchmark.apiKeyDecideSubscribe();
                // Oauth2
                benchmark.oAuth2DecideOnce();
                benchmark.oAuth2DecideSubscribe();
            }
        }
    }
}
