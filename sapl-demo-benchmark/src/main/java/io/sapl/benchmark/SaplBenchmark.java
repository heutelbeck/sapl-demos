package io.sapl.benchmark;

import lombok.extern.slf4j.Slf4j;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static io.sapl.benchmark.report.ReportGenerator.generateHTMLReport;

@Slf4j
public class SaplBenchmark {
    private final BenchmarkConfiguration config;
    private GenericContainer<?> pdpContainer;
    private GenericContainer<?> oauth2Container;
    private final String benchmarkFolder;


    public SaplBenchmark(String cfgFilePath, String benchmarkFolder) throws IOException {
        this.config = BenchmarkConfiguration.fromFile(cfgFilePath);
        this.benchmarkFolder = benchmarkFolder;
        Files.createDirectories(Paths.get(benchmarkFolder));
        var sourceFile = new File(cfgFilePath);
        FileUtils.copyFile(sourceFile, new File(benchmarkFolder+"/"+sourceFile.getName()));
    }

    @SuppressWarnings("rawtypes")
    private GenericContainer<?> getServerLtContainer(){
        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        var container = new GenericContainer(DockerImageName.parse(config.docker_pdp_image))
                .withClasspathResourceMapping("keystore.p12", "/pdp/keystore.p12", BindMode.READ_ONLY)
                .withClasspathResourceMapping("policies/", "/pdp/data/", BindMode.READ_ONLY)
                .withEnv("io_sapl_pdp_embedded_policies-path", "/pdp/data")
                .withEnv("spring_profiles_active", "local")
                .withExposedPorts(config.DOCKER_HTTP_PORT, config.DOCKER_RSOCKET_PORT)
                .waitingFor(Wait.forListeningPort())

                // http settings
                .withEnv("server_address", "0.0.0.0")
                .withEnv("server_port", String.valueOf(config.DOCKER_HTTP_PORT))
                .withEnv("server_ssl_enabled", String.valueOf(config.docker_use_ssl))
                .withEnv("server_ssl_key-store-type", "PKCS12")
                .withEnv("server_ssl_key-store", "/pdp/keystore.p12")
                .withEnv("server_ssl_key-store-password", "benchmarkonly")
                .withEnv("server_ssl_key-alias", "tomcat")

                // rsocket settings
                .withEnv("spring_rsocket_server_address", "0.0.0.0")
                .withEnv("spring_rsocket_server_port", String.valueOf(config.DOCKER_RSOCKET_PORT))
                .withEnv("spring_rsocket_server_ssl_enabled", String.valueOf(config.docker_use_ssl))
                .withEnv("spring_rsocket_server_ssl_key-store-type", "PKCS12")
                .withEnv("spring_rsocket_server_ssl__key-store", "/pdp/keystore.p12")
                .withEnv("spring_rsocket_server_ssl__key-store-password", "benchmarkonly")
                .withEnv("spring_rsocket_server_ssl__key-alias", "tomcat")

                // logging settings
                .withEnv("LOGGING_LEVEL_ROOT", "ERROR")
                .withEnv("LOGGING_LEVEL_ORG_SPRINGFRAMEWORK", "ERROR")
                .withEnv("LOGGING_LEVEL_IO_SAPL", "ERROR");

        // auth Settings
        container.withEnv("io_sapl_server-lt_allowNoAuth", String.valueOf(config.useNoAuth));
        container.withEnv("io_sapl_server-lt_allowBasicAuth", String.valueOf(config.useBasicAuth));
        if ( config.useBasicAuth ) {
            container.withEnv("io_sapl_server-lt_key", config.basic_client_key)
                    .withEnv("io_sapl_server-lt_secret", encoder.encode(config.basic_client_secret));
        }
        container.withEnv("io_sapl_server-lt_allowApiKeyAuth", String.valueOf(config.useAuthApiKey));
        if ( config.useAuthApiKey ) {
            container.withEnv("io_sapl_server-lt_apiKeyHeader", config.api_key_header)
                    .withEnv("io_sapl_server-lt_allowedApiKeys", config.api_key);
        }
        container.withEnv("io_sapl_server-lt_allowOauth2Auth", String.valueOf(config.useOauth2));
        if ( config.useOauth2) {
            String  jwtIssuerUrl;
            if ( config.oauth2_mock_server) {
                jwtIssuerUrl = "http://auth-host:" + oauth2Container.getMappedPort(8080) + "/default";
            } else {
                jwtIssuerUrl = config.oauth2_issuer_url;
            }
            container.withExtraHost("auth-host", "host-gateway")
                     .withEnv("spring_security_oauth2_resourceserver_jwt_issuer-uri", jwtIssuerUrl);
        }
        return container;
    }

    @SuppressWarnings("rawtypes")
    private GenericContainer<?> getOauth2Container(){
        oauth2Container = new GenericContainer(DockerImageName.parse(config.oauth2_mock_image))
                .withExposedPorts(8080)
                .waitingFor(Wait.forListeningPort());
        return oauth2Container;
    }

    void startResponseTimeBenchmark(BenchmarkExecutionContext context) throws RunnerException {
        ChainedOptionsBuilder builder = new OptionsBuilder()
                .include(config.getBenchmarkPattern());
        builder.param("contextJsonString", context.toJsonString());
        builder.jvmArgs(config.jvm_args.toArray(new String[0]))
                .shouldFailOnError(config.fail_on_error)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .resultFormat(ResultFormatType.JSON)
                .result(benchmarkFolder + "/average_response.json")
                .output(benchmarkFolder + "/average_response.log")
                .shouldDoGC(true)
                .forks(config.forks)
                .warmupTime(TimeValue.seconds(config.responseTimeWarmupSeconds))
                .warmupIterations(config.responseTimeWarmupIterations)
                .syncIterations(true)
                .measurementIterations(config.responseTimeMeasurementIterations)
                .measurementTime(TimeValue.seconds(config.responseTimeMeasurementSeconds));
        var benchmarkOptions = builder.build();
        new Runner(benchmarkOptions).run();
    }

    void startThroughputBenchmark(BenchmarkExecutionContext context) throws RunnerException {
        for ( int threads: config.throughputThreadList){
            var builder = new OptionsBuilder()
                    .include(config.getBenchmarkPattern())
                    .param("contextJsonString", context.toJsonString())
                    .jvmArgs(config.jvm_args.toArray(new String[0]))
                    .shouldFailOnError(config.fail_on_error)
                    .mode(Mode.Throughput)
                    .timeUnit(TimeUnit.SECONDS)
                    .resultFormat(ResultFormatType.JSON)
                    .result(benchmarkFolder + "/throughput_" + threads + "threads.json")
                    .output(benchmarkFolder + "/throughput_" + threads + "threads.log")
                    .shouldDoGC(true)
                    .threads(threads)
                    .forks(config.forks)
                    .warmupTime(TimeValue.seconds(config.throughputWarmupSeconds))
                    .warmupIterations(config.throughputWarmupIterations)
                    .syncIterations(true)
                    .measurementIterations(config.throughputMeasurementIterations)
                    .measurementTime(TimeValue.seconds(config.throughputMeasurementSeconds));
            var benchmarkOptions = builder.build();
            new Runner(benchmarkOptions).run();
        }
    }

    void generateBenchmarkReports() throws IOException {
        generateHTMLReport(benchmarkFolder);
    }

    private void startBenchmarks() throws RunnerException {
        var context = config.getBenchmarkExecutionContext(pdpContainer, oauth2Container);
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
        if ( config.useOauth2 && config.oauth2_mock_server) {
            oauth2Container = getOauth2Container();
            oauth2Container.start();
        }
        if ( config.requiredDockerEnvironment() ) {
            pdpContainer= getServerLtContainer();
            pdpContainer.start();
        }
    }

    private void stopContainersIfRunning() {
        // stop containers if running
        if (pdpContainer!= null) {
            try {
                pdpContainer.stop();
            } catch  (Exception e) {
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
