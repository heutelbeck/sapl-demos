package io.sapl.demo.mqtt;

import io.sapl.api.pip.StaticPolicyInformationPointSupplier;
import io.sapl.extensions.mqtt.MqttPolicyInformationPoint;
import io.sapl.extensions.mqtt.SaplMqttClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SaplMqttConfiguration {

    @Bean
    MqttPolicyInformationPoint mqttPolicyInformationPoint() {
        return new MqttPolicyInformationPoint(new SaplMqttClient());
    }

    @Bean
    StaticPolicyInformationPointSupplier staticPips() {
        return List::of;
    }

}
