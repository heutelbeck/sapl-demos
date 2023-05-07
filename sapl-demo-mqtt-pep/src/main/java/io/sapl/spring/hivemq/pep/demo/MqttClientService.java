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

package io.sapl.spring.hivemq.pep.demo;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PayloadFormatIndicator;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@DependsOn("mqttBrokerLifecycleService")
public class MqttClientService implements DisposableBean {

	Mqtt5AsyncClient mqttClient;

	public MqttClientService() throws InterruptedException, ExecutionException {
		mqttClient = Mqtt5Client.builder()
				.identifier(UUID.randomUUID().toString())
				.serverHost("localhost")
				.serverPort(1883)
				.buildAsync();
		log.debug("Connect MQTT client... ");
		var connAckMessage = mqttClient.connect().get(); // Block for Demo
		if (connAckMessage.getReasonCode() != Mqtt5ConnAckReasonCode.SUCCESS) {
			throw new IllegalStateException("Connection to the mqtt broker couldn't be established:" +
					connAckMessage.getReasonCode());
		}
		log.debug("Connect MQTT client... success");
	}

	public Mono<Mqtt5PublishResult> publish(String topic, String payload, boolean retain) {
		return Mono.fromFuture(() -> mqttClient.publish(buildMqttPublishMessage(topic, payload, retain)));
	}

	public Mqtt5AsyncClient getMqttClient() {
		return mqttClient;
	}

	private static Mqtt5Publish buildMqttPublishMessage(String topic, String payload, boolean retain) {
		return Mqtt5Publish.builder()
				.topic(topic)
				.qos(MqttQos.AT_MOST_ONCE)
				.retain(retain)
				.payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
				.payload(payload.getBytes(StandardCharsets.UTF_8))
				.build();
	}

	public static Mqtt5Subscribe buildMqttSubscribeMessage(String topic, int qos) {
		MqttQos mqttQos = Objects.requireNonNullElse(MqttQos.fromCode(qos), MqttQos.AT_MOST_ONCE);
		return Mqtt5Subscribe.builder()
				.topicFilter(topic)
				.qos(mqttQos)
				.build();
	}

	@Override
	public void destroy() throws InterruptedException, ExecutionException {
		log.debug("Disconnect MQTT client");
		mqttClient.disconnect().get();// Block for Demo
	}
}
