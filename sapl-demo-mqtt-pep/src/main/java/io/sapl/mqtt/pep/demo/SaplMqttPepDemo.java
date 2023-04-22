package io.sapl.mqtt.pep.demo;/*
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

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.exceptions.Mqtt5SubAckException;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import io.sapl.interpreter.InitializationException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static io.sapl.mqtt.pep.demo.util.MqttUtility.*;

/**
 * This demo shows how the sapl mqtt pep can be used.
 */
@Slf4j
public class SaplMqttPepDemo {
    private static final String SAPL_EXTENSION_CONFIG_PATH = "sapl-mqtt-pep-demo/src/main/resources";
    private static final String SAPL_PDP_POLICIES_PATH = "sapl-mqtt-pep-demo/src/main/resources/policies";
    private static final String WEATHER_STATION_CLIENT_ID = "weather_station";
    private static final String THERMOMETER_CLIENT_ID = "outdoor_thermometer";
    private static final String TEMPERATURE_TOPIC = "temperature";
    private static final String HUMIDITY_TOPIC = "humidity";
    private static final String TEMPERATURE_PAYLOAD = "24℃";

    /**
     * Starts a demo of the sapl mqtt pep.
     * @param args the configuration parameters will not be evaluated
     * @throws InitializationException is thrown in case the pdp could not be build
     * @throws InterruptedException is thrown in case an interrupt happens while waiting for a published mqtt message
     */
    public static void main(String[] args) throws InitializationException, InterruptedException {

        // start broker
        log.info("Starting the embedded HiveMQ broker.");
        startHivemqBrokerWithPepExtension(SAPL_PDP_POLICIES_PATH, SAPL_EXTENSION_CONFIG_PATH);

        // start clients
        log.info("Starting two mqtt clients ('{}', '{}') and connect them to the mqtt broker.",
                WEATHER_STATION_CLIENT_ID, THERMOMETER_CLIENT_ID);
        Mqtt5BlockingClient weatherStationClient = startMqttClient(WEATHER_STATION_CLIENT_ID);
        Mqtt5BlockingClient thermometerClient = startMqttClient(THERMOMETER_CLIENT_ID);

        // subscribe to topic
        log.info("Let client '{}' subscribe to messages of topic '{}'.",
                WEATHER_STATION_CLIENT_ID, TEMPERATURE_TOPIC);
        Mqtt5Subscribe temperatureSubscribeMessage = buildMqttSubscribeMessage(TEMPERATURE_TOPIC);
        Mqtt5SubAck subAckMessage = weatherStationClient.subscribe(temperatureSubscribeMessage);
        log.info("Client '{}' successfully subscribed to topic '{}' with reason code '{}'.",
                WEATHER_STATION_CLIENT_ID, TEMPERATURE_TOPIC, subAckMessage.getReasonCodes().get(0));

        // publish topic
        log.info("Let client '{}' publish temperature data with topic '{}'.",
                THERMOMETER_CLIENT_ID, TEMPERATURE_TOPIC);
        Mqtt5Publish temperaturePublishMessage = buildMqttPublishMessage(TEMPERATURE_TOPIC, TEMPERATURE_PAYLOAD);
        thermometerClient.publish(temperaturePublishMessage);

        Mqtt5Publish receivedTemperatureMessage =
                weatherStationClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED).receive();
        log.info("Client '{}' received message of topic '{}' with data '{}' of content type '{}'.",
                WEATHER_STATION_CLIENT_ID, TEMPERATURE_TOPIC,
                new String(receivedTemperatureMessage.getPayloadAsBytes(), StandardCharsets.UTF_8),
                receivedTemperatureMessage.getContentType().orElse(null));

        // subscribe to illegal topic
        log.info("Let client '{}' try to subscribe to an illegal topic '{}'.",
                WEATHER_STATION_CLIENT_ID, HUMIDITY_TOPIC);
        Mqtt5Subscribe humiditySubscribeMessage = buildMqttSubscribeMessage(HUMIDITY_TOPIC);
        try {
            weatherStationClient.subscribe(humiditySubscribeMessage);
        } catch (Mqtt5SubAckException subAckException) {
            log.info("Subscription of topic '{}' of client '{}' was denied with reason code '{}' " +
                            "and reason string '{}'.", HUMIDITY_TOPIC, WEATHER_STATION_CLIENT_ID,
                    subAckException.getMqttMessage().getReasonCodes().get(0),
                    subAckException.getMqttMessage().getReasonString().orElse(null));
        }
    }
}