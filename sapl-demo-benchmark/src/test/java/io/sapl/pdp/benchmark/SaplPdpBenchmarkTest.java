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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;

import io.sapl.benchmark.util.BenchmarkException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertTrue(tmpReportPathFile.mkdir());
    }

    @Test
    void whenExecutingEmbeddedBenchmark_withValidSubscription_thenDecisionIsAccepted()
            throws InitializationException, IOException {
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.setRunHttpBenchmarks(false);
        benchmarkConfig.setRunRsocketBenchmarks(false);
        benchmarkConfig
                .setSubscription("{\"subject\": \"Willi\", \"action\": \"requests\", \"resource\": \"information\"}");
        var embeddedBenchmark = new EmbeddedBenchmark();
        var benchmarkContext = BenchmarkExecutionContext.fromBenchmarkConfiguration(benchmarkConfig);
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            embeddedBenchmark.setup();
            assertDoesNotThrow(() -> {
                embeddedBenchmark.noAuthDecideOnce();
                embeddedBenchmark.noAuthDecideSubscribe();
            });
        }
    }

    @Test
    void whenExecutingEmbeddedBenchmark_withInvalidSubscription_thenExceptionIsThrown()
            throws InitializationException, IOException {
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.setRunHttpBenchmarks(false);
        benchmarkConfig.setRunRsocketBenchmarks(false);
        benchmarkConfig.setSubscription(
                "{\"subject\": \"Willi\", \"action\": \"invalid action\", \"resource\": \"information\"}");
        var embeddedBenchmark = new EmbeddedBenchmark();
        var benchmarkContext = BenchmarkExecutionContext.fromBenchmarkConfiguration(benchmarkConfig);
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            embeddedBenchmark.setup();
            assertThrows(BenchmarkException.class, embeddedBenchmark::noAuthDecideOnce);
            assertThrows(BenchmarkException.class, embeddedBenchmark::noAuthDecideSubscribe);
        }
    }

    @Test
    void whenLoadingContaxtFromString_withInvalidJson_thenExcpetionIsThrown() {
        assertThrows(BenchmarkException.class, () -> BenchmarkExecutionContext.fromString("{invalidjson]"));
    }

    @Test
    void whenExecutingHttpBenchmark_thenDecisionIsAccepted() throws IOException {
        var mockedContainer = Mockito.mock(GenericContainer.class);
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.setRunEmbeddedBenchmarks(true);
        benchmarkConfig.setRunHttpBenchmarks(true);
        benchmarkConfig.setRunHttpBenchmarks(true);
        benchmarkConfig.setRunRsocketBenchmarks(false);
        benchmarkConfig.setUseBasicAuth(true);
        benchmarkConfig.setBasicClientKey("123");
        benchmarkConfig.setBasicClientSecret("123");
        benchmarkConfig.setUseAuthApiKey(true);
        benchmarkConfig.setApiKeyHeader("API_KEY");
        benchmarkConfig.setApiKeySecret("123");
        benchmarkConfig.setUseOauth2(true);
        var benchmark = new HttpBenchmark();
        var benchmarkContext = BenchmarkExecutionContext.fromBenchmarkConfiguration(benchmarkConfig, mockedContainer, mockedContainer);
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            benchmark.setup();
            try (MockedStatic<Helper> mockedHelper = Mockito.mockStatic(Helper.class)) {
                mockedHelper.when(() -> Helper.decide(any(), any())).then(__ -> null);
                mockedHelper.when(() -> Helper.decideOnce(any(), any())).then(__ -> null);
                assertDoesNotThrow(() -> {
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
                });
            }
        }
    }

    @Test
    void whenExecutingRsocketBenchmark_thenDecisionIsAccepted() throws IOException {
        var mockedContainer = Mockito.mock(GenericContainer.class);
        Mockito.when(mockedContainer.getHost()).thenReturn("localhost");
        var benchmarkConfig = BenchmarkConfiguration.fromFile("src/test/resources/test_benchmark_config.yaml");
        benchmarkConfig.setRunEmbeddedBenchmarks(false);
        benchmarkConfig.setRunHttpBenchmarks(false);
        benchmarkConfig.setRunRsocketBenchmarks(true);
        benchmarkConfig.setUseBasicAuth(true);
        benchmarkConfig.setBasicClientKey("123");
        benchmarkConfig.setBasicClientSecret("123");
        benchmarkConfig.setUseAuthApiKey(true);
        benchmarkConfig.setApiKeyHeader("API_KEY");
        benchmarkConfig.setApiKeySecret("123");
        var benchmark = new RsocketBenchmark();
        var benchmarkContext = BenchmarkExecutionContext.fromBenchmarkConfiguration(benchmarkConfig, mockedContainer, mockedContainer);
        try (MockedStatic<BenchmarkExecutionContext> utilities = Mockito.mockStatic(BenchmarkExecutionContext.class)) {
            utilities.when(() -> BenchmarkExecutionContext.fromString(any())).thenReturn(benchmarkContext);
            benchmark.setup();
            try (MockedStatic<Helper> mockedHelper = Mockito.mockStatic(Helper.class)) {
                mockedHelper.when(() -> Helper.decide(any(), any())).then(__ -> null);
                mockedHelper.when(() -> Helper.decideOnce(any(), any())).then(__ -> null);
                assertDoesNotThrow(() -> {
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
                });
            }
        }
    }
}
