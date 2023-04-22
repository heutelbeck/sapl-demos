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

package io.sapl.mqtt.pep.demo.util;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import com.hivemq.embedded.EmbeddedExtension;
import com.hivemq.embedded.EmbeddedHiveMQ;
import com.hivemq.embedded.EmbeddedHiveMQBuilder;
import io.sapl.interpreter.InitializationException;
import io.sapl.mqtt.pep.HivemqPepExtensionMain;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * This utility class provides HiveMQ specific functions for the sapl mqtt pep demo.
 */
@UtilityClass
public class MqttUtility {

    private static final String MQTT_SERVER_HOST = "localhost";
    private static final int MQTT_SERVER_PORT = 1883;

    /**
     * Starts an embedded HiveMq broker with the sapl mqtt pep extension.
     * @param saplPdpPoliciesPath the path to the sapl policies
     * @param saplExtensionConfigPath the path to the extension configuration file
     */
    public static void startHivemqBrokerWithPepExtension(String saplPdpPoliciesPath, String saplExtensionConfigPath) {
        var embeddedExtensionBuild = buildEmbeddedSaplMqttPepExtension(
                saplPdpPoliciesPath, saplExtensionConfigPath);
        var embeddedHiveMQBuilder = buildEmbeddedHivemqBroker(embeddedExtensionBuild);

        // start hivemq broker with pep extension
        embeddedHiveMQBuilder.build().start().join();
    }

    private static EmbeddedExtension buildEmbeddedSaplMqttPepExtension(String saplPdpPoliciesPath, String saplExtensionConfigPath) {
        return EmbeddedExtension.builder()
                .withId("SAPL-MQTT-PEP")
                .withName("SAPL-MQTT-PEP")
                .withVersion("1.0.0")
                .withPriority(0)
                .withStartPriority(1000)
                .withAuthor("Nils Mahnken")
                .withExtensionMain(new HivemqPepExtensionMain(saplPdpPoliciesPath, saplExtensionConfigPath))
                .build();
    }

    private static EmbeddedHiveMQBuilder buildEmbeddedHivemqBroker(EmbeddedExtension embeddedExtensionBuild) {
        return EmbeddedHiveMQ.builder()
                .withConfigurationFolder(Path.of("sapl-mqtt-pep-demo/src/main/resources/embedded-config-folder"))
                .withDataFolder(Path.of("sapl-mqtt-pep-demo/src/main/resources/embedded-data-folder"))
                .withExtensionsFolder(Path.of("sapl-mqtt-pep-demo/src/main/resources/embedded-extensions-folder"))
                .withEmbeddedExtension(embeddedExtensionBuild);
    }

    /**
     * Starts a HiveMQ client.
     * @param mqttClientId the client id of the client to start
     * @return returns a blocking mqtt client
     * @throws InitializationException is thrown in case the connection to the mqtt broker failed
     */
    public static Mqtt5BlockingClient startMqttClient(String mqttClientId) throws InitializationException {
        Mqtt5BlockingClient blockingMqttClient = Mqtt5Client.builder()
                .identifier(mqttClientId)
                .serverHost(MQTT_SERVER_HOST)
                .serverPort(MQTT_SERVER_PORT)
                .buildBlocking();
        Mqtt5ConnAck connAckMessage = blockingMqttClient.connect();
        if (connAckMessage.getReasonCode() != Mqtt5ConnAckReasonCode.SUCCESS) {
            throw new InitializationException("Connection to the mqtt broker couldn't be established" +
                    "with reason code: " + connAckMessage.getReasonCode());
        }
        return blockingMqttClient;
    }

    /**
     * Builds a mqtt subscription message.
     * @param topic the topic to subscribe to
     * @return the build mqtt subscription message
     */
    public static Mqtt5Subscribe buildMqttSubscribeMessage(String topic) {
        return Mqtt5Subscribe.builder()
                .topicFilter(topic)
                .qos(MqttQos.AT_MOST_ONCE)
                .build();
    }

    /**
     * Builds a mqtt publish message.
     * @param topic the topic to publish under
     * @param payload the payload to publish
     * @return the build mqtt publish message
     */
    public static Mqtt5Publish buildMqttPublishMessage(String topic, String payload) {
        return Mqtt5Publish.builder()
                .topic(topic)
                .qos(MqttQos.AT_MOST_ONCE)
                .retain(false)
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .build();
    }
}
