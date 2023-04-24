/*
 * Copyright © 2019-2022 Dominic Heutelbeck (dominic@heutelbeck.com)
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

package io.sapl.mqtt.pip.demo;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hivemq.embedded.EmbeddedHiveMQ;
import io.sapl.mqtt.pip.demo.helper.MqttTogglePip;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.interpreter.InitializationException;
import io.sapl.pdp.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.PolicyDecisionPointFactory;
import io.sapl.interpreter.pip.MqttPolicyInformationPoint;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * This demo shows how the MqttPolicyInformationPoint can be used with an embedded PDP.
 */
public class MqttPipDemo {

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static final String SUBJECT = "subjectName";
	private static final String TOPIC = "single_topic";
	private static final String[] TOPICS = {"multiple_topic_1", "multiple_topic_2"};
	private static final ObjectNode RESOURCE = buildJsonResource();
	private static final Logger logger = (Logger) LoggerFactory.getLogger(MqttPipDemo.class);

	/**
	 * Starts a demo of the sapl mqtt pip.
	 * @param args the configuration parameters will not be evaluated
	 * @throws InitializationException is thrown in case the pdp could not be build
	 */
	public static void main(String[] args) throws InitializationException {
		startMqttServerCe();
		EmbeddedPolicyDecisionPoint pdp = buildPdp();

		var authzSubscriptionForPolicyMqttBroker1 = AuthorizationSubscription.of(SUBJECT,
				"actionBroker1", RESOURCE);
		var authzSubscriptionForPolicyMqttToggle = AuthorizationSubscription.of(SUBJECT,
				"actionToggle", RESOURCE);

		pdp.decide(authzSubscriptionForPolicyMqttBroker1)
				.subscribe(authorizationDecision -> handleAuthorizationDecision(authorizationDecision,
						"Policy mqttBroker1"));
		pdp.decide(authzSubscriptionForPolicyMqttToggle)
				.subscribe(authorizationDecision -> handleAuthorizationDecision(authorizationDecision,
						"Policy mqttToggle"));

		pdp.dispose();
	}

	private static void startMqttServerCe() {
		final var embeddedHiveMQBuilder = EmbeddedHiveMQ.builder()
				.withConfigurationFolder(Path.of("sapl-demo-mqtt-pip/src/main/resources/embedded-config-folder"))
				.withDataFolder(Path.of("sapl-demo-mqtt-pip/src/main/resources/embedded-data-folder"))
				.withExtensionsFolder(Path.of("sapl-demo-mqtt-pip/src/main/resources/embedded-extensions-folder"));

		embeddedHiveMQBuilder.build().start().join();
	}

	private static EmbeddedPolicyDecisionPoint buildPdp() throws InitializationException {
		return PolicyDecisionPointFactory
				.filesystemPolicyDecisionPoint("sapl-demo-mqtt-pip/src/main/resources/policies",
						List.of(new MqttPolicyInformationPoint(), new MqttTogglePip()), List.of());
	}

	private static void handleAuthorizationDecision(AuthorizationDecision authzDecision, String name) {
		logger.info("Decision for policy '{}': {}",
				name, authzDecision.getDecision());
	}

	private static ObjectNode buildJsonResource() {
		var resource = JSON.objectNode();
		resource.put("topic", TOPIC);

		var topics = JSON.arrayNode();
		for (String topic : TOPICS) {
			topics.add(topic);
		}
		resource.set("topics", topics);

		return resource;
	}
}