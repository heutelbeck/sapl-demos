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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.benchmark.util.BenchmarkException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BenchmarkConfiguration {
    private final ObjectMapper  mapper        = new ObjectMapper();
    private static final String ENABLED = "enabled";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String DOCKER = "docker";
    private static final String REMOTE = "remote";
    private String benchmarkTarget = DOCKER;

    private static void failOnFurtherMapEntries(Set<String> keyList, String parentEntryPath){
        for (String key : keyList) {
            if ( key != null){
                throw new BenchmarkException("Unknown configuration entry " + parentEntryPath +"." + key);
            }
        }
    }

    // ---------------------------
    // - Connectivity setup
    // ---------------------------
    @JsonProperty("target")
    public void setBenchmarkTarget(String target) {
        if (target.equals(DOCKER) || target.equals(REMOTE)) {
            this.benchmarkTarget = target;
        } else {
            throw new BenchmarkException("invalid target=" + target);
        }
    }

    @Getter
    private String dockerPdpImage;
    @Getter
    private String oauth2MockImage;

    public static final int DOCKER_DEFAULT_RSOCKET_PORT = 7000;
    public static final int DOCKER_DEFAULT_HTTP_PORT = 8080;
    @Getter
    private boolean dockerUseSsl = true;

    @JsonProperty(DOCKER)
    public void setDocker(Map<String, String> map) {
        this.dockerPdpImage = map.remove("pdp_image");
        this.dockerUseSsl = Boolean.parseBoolean(map.remove("use_ssl"));
        failOnFurtherMapEntries(map.keySet(), DOCKER);
    }

    @Getter
    private String remoteBaseUrl;
    @Getter
    private String remoteRsocketHost;
    @Getter
    private int remoteRsocketPort;
    @Getter
    private boolean remoteUseSsl;

    @JsonProperty(REMOTE)
    public void setRemote(Map<String, String> map) {
        this.remoteBaseUrl = map.remove("base_url");
        this.remoteRsocketHost = map.remove("rsocket_host");
        this.remoteRsocketPort = Integer.parseInt(map.remove("rsocket_port"));
        this.remoteUseSsl = Boolean.parseBoolean(map.remove("use_ssl"));
        failOnFurtherMapEntries(map.keySet(), REMOTE);
    }

    // ---------------------------
    // - Subscription
    // ---------------------------
    @Getter
    private AuthorizationSubscription authorizationSubscription = AuthorizationSubscription.of("Willi", "eat", "apple");

    @JsonSetter("subscription")
    public void setSubscription(String subscription) throws JsonProcessingException {
        this.authorizationSubscription = mapper.readValue(subscription, AuthorizationSubscription.class);
    }

    // ---------------------------
    // - Benchmark scope
    // ---------------------------
    @Setter
    private boolean runEmbeddedBenchmarks        = true;
    @Setter
    private boolean runHttpBenchmarks            = true;
    @Setter
    private boolean runRsocketBenchmarks         = true;
    private boolean runDecideOnceBenchmarks      = true;
    private boolean runDecideSubscribeBenchmarks = true;

    @JsonProperty("benchmark_pdp")
    public void setBenchmarkPdp(Map<String, String> map) {
        this.runEmbeddedBenchmarks = Boolean.parseBoolean(map.remove("embedded"));
        this.runHttpBenchmarks     = Boolean.parseBoolean(map.remove("http"));
        this.runRsocketBenchmarks  = Boolean.parseBoolean(map.remove("rsocket"));
        failOnFurtherMapEntries(map.keySet(), "benchmark_pdp");
    }

    @JsonProperty("decision_method")
    public void setDecisionMethod(Map<String, String> map) {
        this.runDecideOnceBenchmarks      = Boolean.parseBoolean(map.remove("decide_once"));
        this.runDecideSubscribeBenchmarks = Boolean.parseBoolean(map.remove("decide_subscribe"));
        failOnFurtherMapEntries(map.keySet(), "decision_method");
    }

    // ---------------------------
    // - Authentication
    // ---------------------------
    @Getter @Setter
    private boolean useNoAuth        = true;
    @Getter @Setter
    private boolean useBasicAuth     = true;
    @Getter @Setter
    private boolean useAuthApiKey    = true;
    @Getter @Setter
    private boolean useOauth2        = false;
    @Getter @Setter
    private String  basicClientKey;
    @Getter @Setter
    private String  basicClientSecret;
    @Getter @Setter
    private String  apiKeyHeader;
    @Getter @Setter
    private String apiKeySecret;
    @Getter
    private boolean oauth2MockServer = true;
    @Getter
    private String  oauth2ClientId;
    @Getter
    private String  oauth2ClientSecret;
    @Getter
    private String  oauth2Scope;
    @Getter
    private String  oauth2TokenUri;
    @Getter
    private String  oauth2IssuerUrl;

    @JsonProperty("noauth")
    public void setNoAuth(Map<String, String> map) {
        this.useNoAuth = Boolean.parseBoolean(map.remove(ENABLED));
        failOnFurtherMapEntries(map.keySet(), "noauth");
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
        failOnFurtherMapEntries(map.keySet(), "basic");
    }

    @JsonProperty("apikey")
    public void setApiKey(Map<String, String> map) {
        this.useAuthApiKey = Boolean.parseBoolean(map.remove(ENABLED));
        if (this.useAuthApiKey) {
            this.apiKeyHeader = map.get("api_key_header");
            this.apiKeySecret = map.get("api_key");
        }
        map.remove("api_key_header");
        map.remove("api_key");
        failOnFurtherMapEntries(map.keySet(), "apikey");
    }

    @JsonProperty("oauth2")
    public void setOauth2(Map<String, String> map) {
        this.useOauth2 = Boolean.parseBoolean(map.remove(ENABLED));
        if (this.useOauth2) {
            this.oauth2MockServer   = Boolean.parseBoolean(map.get("mock_server"));
            this.oauth2MockImage = map.get("mock_image");
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
        failOnFurtherMapEntries(map.keySet(), "oauth2");
    }

    // ---------------------------
    // - Gerneral benchamrk settings
    // ---------------------------
    @Getter
    @JsonProperty("forks")
    public Integer      forks         = 2;
    @Getter
    @JsonProperty("jvm_args")
    private List<String> jvmArgs = new ArrayList<>();
    @Getter
    @JsonProperty("fail_on_error")
    private boolean failOnError = false;

    // ---------------------------
    // - Average Response Time
    // ---------------------------
    @Getter
    private Integer responseTimeWarmupSeconds         = 10;
    @Getter
    private Integer responseTimeWarmupIterations      = 2;
    @Getter
    private Integer responseTimeMeasurementSeconds    = 10;
    @Getter
    private Integer responseTimeMeasurementIterations = 10;

    @JsonProperty("response_time")
    public void setResponseTime(Map<String, String> map) {
        this.responseTimeWarmupSeconds         = Integer.valueOf(map.remove("warmup_seconds"));
        this.responseTimeWarmupIterations      = Integer.valueOf(map.remove("warmup_iterations"));
        this.responseTimeMeasurementSeconds    = Integer.valueOf(map.remove("measure_seconds"));
        this.responseTimeMeasurementIterations = Integer.valueOf(map.remove("measure_iterations"));
        failOnFurtherMapEntries(map.keySet(), "response_time");
    }

    // ---------------------------
    // - throughput
    // ---------------------------
    @Getter
    private List<Integer> throughputThreadList            = List.of(1);
    @Getter
    private Integer       throughputWarmupSeconds         = 10;
    @Getter
    private Integer       throughputWarmupIterations      = 2;
    @Getter
    private Integer       throughputMeasurementSeconds    = 10;
    @Getter
    private Integer       throughputMeasurementIterations = 10;

    @JsonProperty("throughput")
    public void setThroughput(Map<String, Object> map) throws JsonProcessingException {
        this.throughputThreadList            = mapper.readValue(String.valueOf(map.remove("threads")),
                new TypeReference<>() {});
        this.throughputWarmupSeconds         = (Integer) map.remove("warmup_seconds");
        this.throughputWarmupIterations      = (Integer) map.remove("warmup_iterations");
        this.throughputMeasurementSeconds    = (Integer) map.remove("measure_seconds");
        this.throughputMeasurementIterations = (Integer) map.remove("measure_iterations");
        failOnFurtherMapEntries(map.keySet(), "throughput");
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
            authMethods.add("noAuth");
        }
        if (useBasicAuth) {
            authMethods.add("basicAuth");
        }
        if (useAuthApiKey) {
            authMethods.add("apiKey");
        }
        if (useOauth2) {
            authMethods.add("oauth2");
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
        return benchmarkTarget.equals(DOCKER) && (runHttpBenchmarks || runRsocketBenchmarks);
    }
}
