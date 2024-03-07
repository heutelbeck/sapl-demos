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
package io.sapl.benchmark.jmh;

import io.netty.channel.ChannelOption;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.BenchmarkExecutionContext;
import io.sapl.pdp.remote.RemoteHttpPolicyDecisionPoint;
import io.sapl.pdp.remote.RemotePolicyDecisionPoint;
import io.sapl.pdp.remote.RemoteRsocketPolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.time.Duration;

import static io.sapl.benchmark.jmh.Helper.*;

@Slf4j
@State(Scope.Benchmark)
public class RsocketBenchmark {
    @Param({ "{}" })
    String contextJsonString;

    private PolicyDecisionPoint       noauthPdp;
    private PolicyDecisionPoint       basicAuthPdp;
    private PolicyDecisionPoint       apiKeyPdp;
    private PolicyDecisionPoint       oauth2Pdp;
    private BenchmarkExecutionContext context;

    private RemoteRsocketPolicyDecisionPoint.RemoteRsocketPolicyDecisionPointBuilder getBaseBuilder() throws SSLException {
        return RemotePolicyDecisionPoint.builder()
            .rsocket()
            .host(context.getRsocketHost())
            .port(context.getRsocketPort())
            .withUnsecureSSL();
    }

    @Setup(Level.Trial)
    public void setup() throws IOException {
        context = BenchmarkExecutionContext.fromString(contextJsonString);
        log.info("initializing pdp connections");
        if (context.isUseNoAuth()) {
            noauthPdp = getBaseBuilder().build();
        }

        if (context.isUseBasicAuth()) {
            basicAuthPdp = getBaseBuilder()
                    .basicAuth(context.getBasicClientKey(), context.getBasicClientSecret())
                    .build();
        }

        if (context.isUseOauth2()) {
            apiKeyPdp = getBaseBuilder()
                    .apiKey(context.getApiKeyHeader(), context.getApiKey())
                    .build();
        }

        if (context.isUseOauth2()) {
            oauth2Pdp = getBaseBuilder()
                    .oauth2(getClientRegistrationRepository(context), "saplPdp")
                    .build();
        }
    }

    @Benchmark
    public void noAuthDecideSubscribe() {
        decide(noauthPdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void noAuthDecideOnce() {
        decideOnce(noauthPdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void basicAuthDecideSubscribe() {
        decide(basicAuthPdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void basicAuthDecideOnce() {
        decideOnce(basicAuthPdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void apiKeyDecideSubscribe() {
        decide(apiKeyPdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void apiKeyDecideOnce() {
        decideOnce(apiKeyPdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void oAuth2DecideSubscribe() {
        decide(oauth2Pdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void oAuth2DecideOnce() {
        decideOnce(oauth2Pdp, context.getAuthorizationSubscription());
    }
}
