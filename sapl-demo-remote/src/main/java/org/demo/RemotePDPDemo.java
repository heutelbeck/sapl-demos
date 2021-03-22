/*******************************************************************************
 * Copyright 2017-2018 Dominic Heutelbeck (dheutelbeck@ftk.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.demo;

import java.util.concurrent.Callable;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.pdp.remote.RemotePolicyDecisionPoint;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class RemotePDPDemo implements Callable<Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(RemotePDPDemo.class);

	@Option(names = { "-h",
			"-host" }, description = "Hostname of the policy decision point including prefix and port. E.g. 'https://example.org:8443'.")
	private String host = "https://localhost:8443";

	// The default option set here are the default credentials of the pdp-server-lt

	@Option(names = { "-k",
			"-key" }, description = "Client key for the demo application, to be obtained from the PDP administrator.")
	private String clientKey = "YJidgyT2mfdkbmL";
	@Option(names = { "-s",
			"-secret" }, description = "Client secret for the demo application, to be obtained from the PDP administrator.")
	private String clientSecret = "Fa4zvYQdiwHZVXh";

	public static void main(String... args) {
		System.exit(new CommandLine(new RemotePDPDemo()).execute(args));
	}

	public Integer call() throws SSLException, InterruptedException {
		LOG.warn("INSECURE SSL SETTINGS! This demo uses an insecure SslContext for "
				+ "testing purposes only. It will accept all certificates. "
				+ "This is only for testing local servers with self-signed certificates easily. "
				+ "NERVER USE SUCH A CONFIURATION IN PRODUCTION!");
		var sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		var pdp = new RemotePolicyDecisionPoint(host, clientKey, clientSecret, sslContext);

		/*
		 * To have the client use the default SSL verification use this constructor
		 * instead, or provide your own TrustManager/SslContext accordingly.
		 * 
		 * var pdp = new RemotePolicyDecisionPoint(host, clientKey, clientSecret);
		 */

		var authzSubscription = AuthorizationSubscription.of("Willi", "eat", "icecream");
		LOG.info("Subscription: {}", authzSubscription);
		/*
		 * This just consumes the first decision in a blocking fashion to quickly
		 * terminate the demo application. If not using blockFirst() or take(1), the
		 * Flux will continue to listen to the PDP server and receive updated
		 * authorization decisions when applicable. For alternative patterns of
		 * invocation, consult the sapl-demo-pdp-embedded
		 */
		pdp.decide(authzSubscription).doOnNext(decision -> LOG.info("Decision: {}", decision)).subscribe();
		Thread.sleep(60*1000);
		return 0;
	}

}
