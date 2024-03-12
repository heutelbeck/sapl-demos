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
package io.sapl.benchmark;

import static io.sapl.benchmark.report.ReportGenerator.generateHTMLReport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaplBenchmark {
    private final BenchmarkConfiguration config;
    private GenericContainer<?>          pdpContainer;
    private GenericContainer<?>          oauth2Container;
    private final String                 benchmarkFolder;

    public SaplBenchmark(String cfgFilePath, String benchmarkFolder) throws IOException {
        this.config          = BenchmarkConfiguration.fromFile(cfgFilePath);
        this.benchmarkFolder = benchmarkFolder;
        Files.createDirectories(Paths.get(benchmarkFolder));
        var sourceFile = new File(cfgFilePath);
        FileUtils.copyFile(sourceFile, new File(benchmarkFolder + File.separator + sourceFile.getName()));
    }

    private GenericContainer<?> getServerLtContainer() {
        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

        var dockerKeystorePath = "/pdp/keystore.p12";

        var errorLogLevel = "ERROR";
        var container     = new GenericContainer(DockerImageName.parse(config.getDockerPdpImage()))
                .withClasspathResourceMapping("keystore.p12", dockerKeystorePath, BindMode.READ_ONLY)
                .withClasspathResourceMapping("policies/", "/pdp/data/", BindMode.READ_ONLY)
                .withEnv("io_sapl_pdp_embedded_policies-path", "/pdp/data").withEnv("spring_profiles_active", "local")
                .withExposedPorts(BenchmarkConfiguration.DOCKER_DEFAULT_HTTP_PORT,
                        BenchmarkConfiguration.DOCKER_DEFAULT_RSOCKET_PORT)
                .waitingFor(Wait.forListeningPort())

                // http settings
                .withEnv("server_address", "0.0.0.0")
                .withEnv("server_port", String.valueOf(BenchmarkConfiguration.DOCKER_DEFAULT_HTTP_PORT))
                .withEnv("server_ssl_enabled", String.valueOf(config.isDockerUseSsl()))
                .withEnv("server_ssl_key-store-type", "PKCS12").withEnv("server_ssl_key-store", dockerKeystorePath)
                .withEnv("server_ssl_key-store-password", "benchmarkonly").withEnv("server_ssl_key-alias", "tomcat")

                // rsocket settings
                .withEnv("spring_rsocket_server_address", "0.0.0.0")
                .withEnv("spring_rsocket_server_port",
                        String.valueOf(BenchmarkConfiguration.DOCKER_DEFAULT_RSOCKET_PORT))
                .withEnv("spring_rsocket_server_ssl_enabled", String.valueOf(config.isDockerUseSsl()))
                .withEnv("spring_rsocket_server_ssl_key-store-type", "PKCS12")
                .withEnv("spring_rsocket_server_ssl__key-store", dockerKeystorePath)
                .withEnv("spring_rsocket_server_ssl__key-store-password", "benchmarkonly")
                .withEnv("spring_rsocket_server_ssl__key-alias", "tomcat")

                // logging settings
                .withEnv("LOGGING_LEVEL_ROOT", errorLogLevel)
                .withEnv("LOGGING_LEVEL_ORG_SPRINGFRAMEWORK", errorLogLevel)
                .withEnv("LOGGING_LEVEL_IO_SAPL", errorLogLevel);

        // auth Settings
        container.withEnv("io_sapl_server-lt_allowNoAuth", String.valueOf(config.isUseNoAuth()));
        container.withEnv("io_sapl_server-lt_allowBasicAuth", String.valueOf(config.isUseBasicAuth()));
        if (config.isUseBasicAuth()) {
            container.withEnv("io_sapl_server-lt_key", config.getBasicClientKey()).withEnv("io_sapl_server-lt_secret",
                    encoder.encode(config.getBasicClientSecret()));
        }
        container.withEnv("io_sapl_server-lt_allowApiKeyAuth", String.valueOf(config.isUseAuthApiKey()));
        if (config.isUseAuthApiKey()) {
            container.withEnv("io_sapl_server-lt_apiKeyHeader", config.getApiKeyHeader())
                    .withEnv("io_sapl_server-lt_allowedApiKeys", config.getApiKeySecret());
        }
        container.withEnv("io_sapl_server-lt_allowOauth2Auth", String.valueOf(config.isUseOauth2()));
        if (config.isUseOauth2()) {
            String jwtIssuerUrl;
            if (config.isOauth2MockServer()) {
                jwtIssuerUrl = "http://auth-host:" + oauth2Container.getMappedPort(8080) + "/default";
            } else {
                jwtIssuerUrl = config.getOauth2IssuerUrl();
            }
            container.withExtraHost("auth-host", "host-gateway")
                    .withEnv("spring_security_oauth2_resourceserver_jwt_issuer-uri", jwtIssuerUrl);
        }
        return container;
    }

    private GenericContainer<?> getOauth2Container() {
        oauth2Container = new GenericContainer<>(DockerImageName.parse(config.getOauth2MockImage()))
                .withExposedPorts(8080).waitingFor(Wait.forListeningPort());
        return oauth2Container;
    }

    void startResponseTimeBenchmark(BenchmarkExecutionContext context) throws RunnerException {
        ChainedOptionsBuilder builder = new OptionsBuilder().include(config.getBenchmarkPattern());
        builder.param("contextJsonString", context.toJsonString());
        builder.jvmArgs(config.getJvmArgs().toArray(new String[0])).shouldFailOnError(config.isFailOnError())
                .mode(Mode.AverageTime).timeUnit(TimeUnit.MILLISECONDS).resultFormat(ResultFormatType.JSON)
                .result(benchmarkFolder + "/average_response.json").output(benchmarkFolder + "/average_response.log")
                .shouldDoGC(true).forks(config.forks)
                .warmupTime(TimeValue.seconds(config.getResponseTimeWarmupSeconds()))
                .warmupIterations(config.getResponseTimeWarmupIterations()).syncIterations(true)
                .measurementIterations(config.getResponseTimeMeasurementIterations())
                .measurementTime(TimeValue.seconds(config.getResponseTimeMeasurementSeconds()));
        var benchmarkOptions = builder.build();
        new Runner(benchmarkOptions).run();
    }

    void startThroughputBenchmark(BenchmarkExecutionContext context) throws RunnerException {
        for (int threads : config.getThroughputThreadList()) {
            new Runner(new OptionsBuilder().include(config.getBenchmarkPattern())
                    .param("contextJsonString", context.toJsonString())
                    .jvmArgs(config.getJvmArgs().toArray(new String[0])).shouldFailOnError(config.isFailOnError())
                    .mode(Mode.Throughput).timeUnit(TimeUnit.SECONDS).resultFormat(ResultFormatType.JSON)
                    .result(benchmarkFolder + "/throughput_" + threads + "threads.json")
                    .output(benchmarkFolder + "/throughput_" + threads + "threads.log").shouldDoGC(true)
                    .threads(threads).forks(config.forks).warmupIterations(config.getThroughputWarmupIterations())
                    .warmupTime(TimeValue.seconds(config.getThroughputWarmupSeconds())).syncIterations(true)
                    .measurementIterations(config.getThroughputMeasurementIterations())
                    .measurementTime(TimeValue.seconds(config.getThroughputWarmupSeconds())).build()).run();
        }
    }

    void generateBenchmarkReports() throws IOException {
        generateHTMLReport(benchmarkFolder);
    }

    private void startBenchmarks() throws RunnerException {
        var context = BenchmarkExecutionContext.fromBenchmarkConfiguration(config, pdpContainer, oauth2Container);
        startResponseTimeBenchmark(context);
        startThroughputBenchmark(context);
    }

    public void executeBenchmark() throws RunnerException {
        try {
            startContainersIfNeeded();
            startBenchmarks();
        } finally {
            stopContainersIfRunning();
        }
    }

    private void startContainersIfNeeded() {
        if (config.isUseOauth2() && config.isOauth2MockServer()) {
            oauth2Container = getOauth2Container();
            oauth2Container.start();
        }
        if (config.requiredDockerEnvironment()) {
            pdpContainer = getServerLtContainer();
            pdpContainer.start();
        }
    }

    private void stopContainersIfRunning() {
        // stop containers if running
        if (pdpContainer != null) {
            try {
                pdpContainer.stop();
            } catch (Exception e) {
                log.error("An exception occurred!", e);
            }
        }
        if (this.oauth2Container != null) {
            try {
                oauth2Container.stop();
            } catch (Exception e) {
                log.error("An exception occurred!", e);
            }
        }
    }
}
