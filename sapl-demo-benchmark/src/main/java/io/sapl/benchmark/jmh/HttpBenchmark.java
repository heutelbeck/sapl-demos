package io.sapl.benchmark.jmh;

import io.netty.channel.ChannelOption;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.BenchmarkExecutionContext;
import io.sapl.pdp.remote.RemotePolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.Duration;

import static io.sapl.benchmark.jmh.Helper.*;


@Slf4j
@State(Scope.Benchmark)
public class HttpBenchmark {
    @Param({"{}"})
    @SuppressWarnings("unused")
    String contextJsonString;

    private PolicyDecisionPoint noauthPdp;
    private PolicyDecisionPoint basicAuthPdp;
    private PolicyDecisionPoint apiKeyPdp;
    private PolicyDecisionPoint oauth2Pdp;
    private BenchmarkExecutionContext context;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        context = BenchmarkExecutionContext.fromString(contextJsonString);
        log.info("initializing pdp connections");
        if ( context.useNoAuth ) {
            noauthPdp = RemotePolicyDecisionPoint.builder()
                    .http()
                    .baseUrl(context.http_base_url)
                    .withHttpClient(HttpClient.create().responseTimeout(Duration.ofSeconds(10)))
                    .withUnsecureSSL()
                    // set SO_LINGER to 0 so that the http sockets are closed immediately -> TIME_WAIT
                    .option(ChannelOption.SO_LINGER, 0)
                    .build();
        }

        if ( context.useBasicAuth ) {
            basicAuthPdp = RemotePolicyDecisionPoint.builder()
                    .http()
                    .baseUrl(context.http_base_url)
                    .withUnsecureSSL()
                    // set SO_LINGER to 0 so that the http sockets are closed immediately -> TIME_WAIT
                    .option(ChannelOption.SO_LINGER, 0)
                    .basicAuth(context.basic_client_key, context.basic_client_secret)
                    .build();
        }

        if ( context.useAuthApiKey ) {
            apiKeyPdp = RemotePolicyDecisionPoint.builder()
                    .http()
                    .baseUrl(context.http_base_url)
                    .withUnsecureSSL()
                    // set SO_LINGER to 0 so that the http sockets are closed immediately -> TIME_WAIT
                    .option(ChannelOption.SO_LINGER, 0)
                    .apiKey(context.api_key_header, context.api_key)
                    .build();
        }

        if ( context.useOauth2 ) {
            oauth2Pdp = RemotePolicyDecisionPoint.builder()
                    .http()
                    .baseUrl(context.http_base_url)
                    .withUnsecureSSL()
                    // set SO_LINGER to 0 so that the http sockets are closed immediately -> TIME_WAIT
                    .option(ChannelOption.SO_LINGER, 0)
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
    public void Oauth2DecideSubscribe() {
        decide(oauth2Pdp, context.authorizationSubscription);
    }

    @Benchmark
    public void  Oauth2DecideOnce() {
        decideOnce(oauth2Pdp, context.authorizationSubscription);
    }
}