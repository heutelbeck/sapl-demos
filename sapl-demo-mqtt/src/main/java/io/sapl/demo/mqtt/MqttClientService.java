package io.sapl.demo.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PayloadFormatIndicator;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@DependsOn("mqttBrokerLifecycleService")
public class MqttClientService implements DisposableBean {

	Mqtt5AsyncClient mqttClient;

	public MqttClientService() throws InterruptedException, ExecutionException {
		mqttClient = Mqtt5Client.builder()
				.identifier("demoClient")
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

	private static Mqtt5Publish buildMqttPublishMessage(String topic, String payload, boolean retain) {
		return Mqtt5Publish.builder()
				.topic(topic)
				.qos(MqttQos.AT_MOST_ONCE)
				.retain(retain)
				.payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
				.payload(payload.getBytes(StandardCharsets.UTF_8))
				.build();
	}

	@Override
	public void destroy() throws InterruptedException, ExecutionException {
		log.debug("Disconnect MQTT client");
		mqttClient.disconnect().get();// Block for Demo
	}

}
