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

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.BenchmarkExecutionContext;
import io.sapl.pdp.remote.RemotePolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

import static io.sapl.benchmark.jmh.Helper.*;


@Slf4j
@State(Scope.Benchmark)
public class RsocketBenchmark {
    @Param({"{}"})
    @SuppressWarnings("unused")
    String contextJsonString;

    private PolicyDecisionPoint noauthPdp;
    private PolicyDecisionPoint basicAuthPdp;
    private PolicyDecisionPoint apiKeyPdp;
    private PolicyDecisionPoint oauth2Pdp;
    @SuppressWarnings("unused")
    private BenchmarkExecutionContext context;


    @Setup(Level.Trial)
    public void setup() throws IOException {
        context = BenchmarkExecutionContext.fromString(contextJsonString);
        log.info("initializing pdp connections");
        if ( context.useNoAuth ) {
            noauthPdp = RemotePolicyDecisionPoint.builder()
                    .rsocket()
                    .host(context.rsocketHost)
                    .port(context.rsocketPort)
                    .withUnsecureSSL()
                    .build();
        }

        if ( context.useBasicAuth ) {
            basicAuthPdp = RemotePolicyDecisionPoint.builder()
                    .rsocket()
                    .host(context.rsocketHost)
                    .port(context.rsocketPort)
                    .withUnsecureSSL()
                    .basicAuth(context.basic_client_key, context.basic_client_secret)
                    .build();
        }

        if ( context.useAuthApiKey ) {
            apiKeyPdp = RemotePolicyDecisionPoint.builder()
                    .rsocket()
                    .host(context.rsocketHost)
                    .port(context.rsocketPort)
                    .withUnsecureSSL()
                    .apiKey(context.api_key_header, context.api_key)
                    .build();
        }

        if ( context.useOauth2 ){
            oauth2Pdp = RemotePolicyDecisionPoint.builder()
                    .rsocket()
                    .host(context.rsocketHost)
                    .port(context.rsocketPort)
                    .withUnsecureSSL()
                    .oauth2(getClientRegistrationRepository(context), "saplPdp")
                    .build();
        }
    }


    @Benchmark
    public void NoAuthDecideSubscribe() {
        decide(noauthPdp, context.authorizationSubscription);
    }

    @Benchmark
    public void NoAuthDecideOnce() {
        decideOnce(noauthPdp, context.authorizationSubscription);
    }

    @Benchmark
    @SuppressWarnings("unused")
    public void BasicAuthDecideSubscribe() {
        decide(basicAuthPdp, context.authorizationSubscription);
    }

    @Benchmark
    public void BasicAuthDecideOnce() {
        decideOnce(basicAuthPdp, context.authorizationSubscription);
    }

    @Benchmark
    public void ApiKeyDecideSubscribe() {
        decide(apiKeyPdp, context.authorizationSubscription);
    }

    @Benchmark
    public void ApiKeyDecideOnce() {
        decideOnce(apiKeyPdp, context.authorizationSubscription);
    }

    @Benchmark
    @SuppressWarnings("unused")
    public void Oauth2DecideSubscribe() {
        decide(oauth2Pdp, context.authorizationSubscription);
    }

    @Benchmark
    @SuppressWarnings("unused")
    public void  Oauth2DecideOnce() {
        decideOnce(oauth2Pdp, context.authorizationSubscription);
    }
}