/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package org.demo;

import java.util.concurrent.Callable;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.MultiAuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.pdp.remote.RemotePolicyDecisionPoint;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class RemotePDPDemo implements Callable<Integer> {

    private static final Logger LOG         = LoggerFactory.getLogger(RemotePDPDemo.class);
    private static final int    RSOCKETPORT = 7000;
    private static final String RSOCKETHOST = "localhost";

    @Option(names = { "-h",
            "-host" }, description = "Hostname of the policy decision point including prefix and port. E.g. 'https://example.org:8443'.")
    private String host = "https://localhost:8443";

    // The default option set here are the default credentials of the pdp-server-lt

    @Option(names = { "-k",
            "-key" }, description = "Client key for the demo application, to be obtained from the PDP administrator.")
    private String clientKey = "mybdpernDvaQHHzSn5VGsg";

    @Option(names = { "-s",
            "-secret" }, description = "Client secret for the demo application, to be obtained from the PDP administrator.")
    private String clientSecret = "lHZ83Uv/8l6BtthixMyeyvzaTbAxe14rF50cvtzRNFY=";

    public static void main(String... args) {
        System.exit(new CommandLine(new RemotePDPDemo()).execute(args));
    }

    public Integer call() throws SSLException, JsonProcessingException {

        PolicyDecisionPoint pdp;

        if (host.startsWith("rsocket")) {
            pdp = RemotePolicyDecisionPoint.builder().rsocket().host(RSOCKETHOST).port(RSOCKETPORT)
                    .basicAuth(clientKey, clientSecret).withUnsecureSSL().build();
        } else {
            pdp = RemotePolicyDecisionPoint.builder().http().baseUrl(host).basicAuth(clientKey, clientSecret)
                    .withUnsecureSSL().build();
        }

        /*
         * To have the client use the default SSL verification use this constructor
         * instead, or provide your own TrustManager/SslContext accordingly.
         *
         * var pdp = new RemotePolicyDecisionPoint(host, clientKey, clientSecret);
         */

        final var authzSubscription = AuthorizationSubscription.of("Willi", "eat", "icecream");
        LOG.info("Subscription: {}", authzSubscription);

        final var multiSubscription = new MultiAuthorizationSubscription()
                .addAuthorizationSubscription("id-1", "bs@simpsons.com", "read",
                        "file://example/med/record/patient/BartSimpson")
                .addAuthorizationSubscription("id-2", "ms@simpsons.com", "read",
                        "file://example/med/record/patient/MaggieSimpson");
        LOG.info("Multi: {}", multiSubscription);
        final var mapper = new ObjectMapper();
        final var json   = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(multiSubscription);
        LOG.info("JSON: {}", json);

        /*
         * This just consumes the first decision in a blocking fashion to quickly
         * terminate the demo application. If not using blockFirst() or take(1), the
         * Flux will continue to listen to the PDP server and receive updated
         * authorization decisions when applicable. For alternative patterns of
         * invocation, consult the sapl-demo-pdp-embedded
         */
        pdp.decide(multiSubscription).doOnNext(decision -> LOG.info("Decision: {}", decision)).blockFirst();
        return 0;
    }

}
