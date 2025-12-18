package io.sapl.demo.mqtt;

import io.sapl.extensions.mqtt.MqttPolicyInformationPoint;
import io.sapl.extensions.mqtt.SaplMqttClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaplMqttConfiguration {

    @Bean
    MqttPolicyInformationPoint mqttPolicyInformationPoint() {
        return new MqttPolicyInformationPoint(new SaplMqttClient());
    }
}
