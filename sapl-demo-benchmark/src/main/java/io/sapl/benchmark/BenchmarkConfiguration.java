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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.testcontainers.containers.GenericContainer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.sapl.api.pdp.AuthorizationSubscription;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BenchmarkConfiguration {
    private static final String ENABLED       = "enabled";
    private static final String CLIENT_SECRET = "client_secret";
    private final ObjectMapper  mapper        = new ObjectMapper();
    public static final String  DOCKER        = "docker";
    public static final String  REMOTE        = "remote";
    public String               target        = DOCKER;

    // ---------------------------
    // - Connectivity setup
    // ---------------------------
    @JsonProperty("target")
    public void setTarget(String target) {
        if (target.equals(DOCKER) || target.equals(REMOTE)) {
            this.target = target;
        } else {
            throw new BenchmarkException("invalid target=" + target);
        }
    }

    public String    docker_pdp_image;
    public String    oauth2_mock_image;
    public final int DOCKER_RSOCKET_PORT = 7000;
    public final int DOCKER_HTTP_PORT    = 8080;
    public boolean   docker_use_ssl      = true;

    @JsonProperty("docker")
    public void setDocker(Map<String, String> map) {
        this.docker_pdp_image = map.remove("pdp_image");
        this.docker_use_ssl   = Boolean.parseBoolean(map.remove("use_ssl"));
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry remote." + key);
        }
    }

    public String  remote_base_url;
    public String  remote_rsocket_host;
    public int     remote_rsocket_port;
    public boolean remote_use_ssl;

    @JsonProperty("remote")
    public void setRemote(Map<String, String> map) {
        this.remote_base_url     = map.remove("base_url");
        this.remote_rsocket_host = map.remove("rsocket_host");
        this.remote_rsocket_port = Integer.parseInt(map.remove("rsocket_port"));
        this.remote_use_ssl      = Boolean.parseBoolean(map.remove("use_ssl"));
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry remote." + key);
        }
    }

    // ---------------------------
    // - Subscription
    // ---------------------------
    public AuthorizationSubscription authorizationSubscription = AuthorizationSubscription.of("Willi", "eat", "apple");

    @JsonProperty("subscription")
    public void setSubscription(String subscription) throws JsonProcessingException {
        this.authorizationSubscription = mapper.readValue(subscription, AuthorizationSubscription.class);
    }

    // ---------------------------
    // - Benchmark scope
    // ---------------------------
    public boolean runEmbeddedBenchmarks        = true;
    public boolean runHttpBenchmarks            = true;
    public boolean runRsocketBenchmarks         = true;
    public boolean runDecideOnceBenchmarks      = true;
    public boolean runDecideSubscribeBenchmarks = true;

    @JsonProperty("benchmark_pdp")
    public void setBenchmarkPdp(Map<String, String> map) {
        this.runEmbeddedBenchmarks = Boolean.parseBoolean(map.remove("embedded"));
        this.runHttpBenchmarks     = Boolean.parseBoolean(map.remove("http"));
        this.runRsocketBenchmarks  = Boolean.parseBoolean(map.remove("rsocket"));
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry benchmark_pdp." + key);
        }
    }

    @JsonProperty("decision_method")
    public void setDecisionMethod(Map<String, String> map) {
        this.runDecideOnceBenchmarks      = Boolean.parseBoolean(map.remove("decide_once"));
        this.runDecideSubscribeBenchmarks = Boolean.parseBoolean(map.remove("decide_subscribe"));
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry decision_method." + key);
        }
    }

    // ---------------------------
    // - Authentication
    // ---------------------------
    public boolean useNoAuth        = true;
    public boolean useBasicAuth     = true;
    public boolean useAuthApiKey    = true;
    public boolean useOauth2        = false;
    public String  basicClientKey;
    public String  basicClientSecret;
    public String  apiKeyHeader;
    public String  apiKey;
    public boolean oauth2MockServer = true;
    public String  oauth2ClientId;
    public String  oauth2ClientSecret;
    public String  oauth2Scope;
    public String  oauth2TokenUri;
    public String  oauth2IssuerUrl;

    @JsonProperty("noauth")
    public void setNoAuth(Map<String, String> map) {
        this.useNoAuth = Boolean.parseBoolean(map.remove(ENABLED));
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry noauth." + key);
        }
    }

    @JsonProperty("basic")
    public void setBasic(Map<String, String> map) {
        this.useBasicAuth = Boolean.parseBoolean(map.remove(ENABLED));
        if (this.useBasicAuth) {
            this.basicClientKey    = map.get("client_key");
            this.basicClientSecret = map.get(CLIENT_SECRET);
        }
        map.remove("client_key");
        map.remove(CLIENT_SECRET);
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry basic." + key);
        }
    }

    @JsonProperty("apikey")
    public void setApiKey(Map<String, String> map) {
        this.useAuthApiKey = Boolean.parseBoolean(map.remove(ENABLED));
        if (this.useAuthApiKey) {
            this.apiKeyHeader = map.get("api_key_header");
            this.apiKey       = map.get("api_key");
        }
        map.remove("api_key_header");
        map.remove("api_key");
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry apikey." + key);
        }
    }

    @JsonProperty("oauth2")
    public void setOauth2(Map<String, String> map) {
        this.useOauth2 = Boolean.parseBoolean(map.remove(ENABLED));
        if (this.useOauth2) {
            this.oauth2MockServer   = Boolean.parseBoolean(map.get("mock_server"));
            this.oauth2_mock_image  = map.get("mock_image");
            this.oauth2ClientId     = map.get("client_id");
            this.oauth2ClientSecret = map.get(CLIENT_SECRET);
            this.oauth2Scope        = map.get("scope");
            if (!oauth2MockServer) {
                this.oauth2TokenUri  = map.get("token_uri");
                this.oauth2IssuerUrl = map.get("issuer_url");
            }
        }
        map.remove("mock_server");
        map.remove("mock_image");
        map.remove("client_id");
        map.remove(CLIENT_SECRET);
        map.remove("scope");
        map.remove("token_uri");
        map.remove("issuer_url");
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry oauth2." + key);
        }
    }

    // ---------------------------
    // - Gerneral benchamrk settings
    // ---------------------------
    @JsonProperty
    public Integer      forks         = 2;
    @JsonProperty
    public List<String> jvm_args      = new ArrayList<>();
    @JsonProperty
    public boolean      fail_on_error = false;

    // ---------------------------
    // - Average Response Time
    // ---------------------------
    public Integer responseTimeWarmupSeconds         = 10;
    public Integer responseTimeWarmupIterations      = 2;
    public Integer responseTimeMeasurementSeconds    = 10;
    public Integer responseTimeMeasurementIterations = 10;

    @JsonProperty("response_time")
    public void setResponseTime(Map<String, String> map) {
        this.responseTimeWarmupSeconds         = Integer.valueOf(map.remove("warmup_seconds"));
        this.responseTimeWarmupIterations      = Integer.valueOf(map.remove("warmup_iterations"));
        this.responseTimeMeasurementSeconds    = Integer.valueOf(map.remove("measure_seconds"));
        this.responseTimeMeasurementIterations = Integer.valueOf(map.remove("measure_iterations"));
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry response_time." + key);
        }
    }

    // ---------------------------
    // - throughput
    // ---------------------------
    public List<Integer> throughputThreadList            = List.of(1);
    public Integer       throughputWarmupSeconds         = 10;
    public Integer       throughputWarmupIterations      = 2;
    public Integer       throughputMeasurementSeconds    = 10;
    public Integer       throughputMeasurementIterations = 10;

    @JsonProperty("throughput")
    public void setThroughput(Map<String, Object> map) throws JsonProcessingException {
        this.throughputThreadList            = mapper.readValue(String.valueOf(map.remove("threads")),
                new TypeReference<>() {
                                                     });
        this.throughputWarmupSeconds         = (Integer) map.remove("warmup_seconds");
        this.throughputWarmupIterations      = (Integer) map.remove("warmup_iterations");
        this.throughputMeasurementSeconds    = (Integer) map.remove("measure_seconds");
        this.throughputMeasurementIterations = (Integer) map.remove("measure_iterations");
        for (String key : map.keySet()) {
            throw new BenchmarkException("Unknown configuration entry throughput." + key);
        }
    }

    @JsonIgnore
    public String getBenchmarkPattern() {
        List<String> classes         = new ArrayList<>();
        List<String> authMethods     = new ArrayList<>();
        List<String> decisionMethods = new ArrayList<>();

        if (runEmbeddedBenchmarks) {
            classes.add("EmbeddedBenchmark");
        }
        if (runHttpBenchmarks) {
            classes.add("HttpBenchmark");
        }
        if (runRsocketBenchmarks) {
            classes.add("RsocketBenchmark");
        }

        if (useNoAuth) {
            authMethods.add("NoAuth");
        }
        if (useBasicAuth) {
            authMethods.add("BasicAuth");
        }
        if (useAuthApiKey) {
            authMethods.add("ApiKey");
        }
        if (useOauth2) {
            authMethods.add("Oauth2");
        }

        if (runDecideOnceBenchmarks) {
            decisionMethods.add("DecideOnce");
        }
        if (runDecideSubscribeBenchmarks) {
            decisionMethods.add("DecideSubscribe");
        }
        String filterRegex = "^io.sapl.benchmark.jmh.(" + StringUtils.join(classes, "|") + ").("
                + StringUtils.join(authMethods, "|") + ")(" + StringUtils.join(decisionMethods, "|") + ")$";
        log.info("filterRegex=" + filterRegex);
        return filterRegex;
    }

    public static BenchmarkConfiguration fromFile(String filePath) throws IOException {
        File               file   = new File(filePath);
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return mapper.readValue(file, BenchmarkConfiguration.class);
    }

    public boolean requiredDockerEnvironment() {
        return target.equals(DOCKER) && (runHttpBenchmarks || runRsocketBenchmarks);
    }

    @SuppressWarnings("rawtypes")
    public BenchmarkExecutionContext getBenchmarkExecutionContext(GenericContainer pdpContainer,
            GenericContainer oauthContainer) {
        var context = new BenchmarkExecutionContext();
        context.authorizationSubscription = authorizationSubscription;
        if (requiredDockerEnvironment()) {
            context.rsocketHost = pdpContainer.getHost();
            context.rsocketPort = pdpContainer.getMappedPort(DOCKER_RSOCKET_PORT);
            context.use_ssl     = docker_use_ssl;
            if (context.use_ssl) {
                context.http_base_url = "https://" + pdpContainer.getHost() + ":"
                        + pdpContainer.getMappedPort(DOCKER_HTTP_PORT);
            } else {
                // noinspection HttpUrlsUsage
                context.http_base_url = "http://" + pdpContainer.getHost() + ":"
                        + pdpContainer.getMappedPort(DOCKER_HTTP_PORT);
            }
        } else {
            context.rsocketHost   = remote_rsocket_host;
            context.rsocketPort   = remote_rsocket_port;
            context.use_ssl       = remote_use_ssl;
            context.http_base_url = remote_base_url;
        }
        context.basic_client_key    = basicClientKey;
        context.basic_client_secret = basicClientSecret;
        context.api_key_header      = apiKeyHeader;
        context.api_key             = apiKey;
        if (useOauth2 && oauth2MockServer) {
            context.oauth2_token_uri = "http://auth-host:" + oauthContainer.getMappedPort(8080) + "/default/token";
        } else {
            context.oauth2_token_uri = oauth2TokenUri;
        }
        context.oauth2_client_id     = oauth2ClientId;
        context.oauth2_client_secret = oauth2ClientSecret;
        context.oauth2_scope         = oauth2Scope;

        context.useNoAuth     = useNoAuth;
        context.useBasicAuth  = useBasicAuth;
        context.useAuthApiKey = useAuthApiKey;
        context.useOauth2     = useOauth2;
        return context;
    }
}
