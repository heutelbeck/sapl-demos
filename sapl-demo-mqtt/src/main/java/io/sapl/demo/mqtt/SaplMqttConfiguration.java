package io.sapl.demo.mqtt;

import java.time.Clock;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import io.sapl.api.pip.StaticPolicyInformationPointSupplier;
import io.sapl.extensions.mqtt.MqttPolicyInformationPoint;

@Configuration
public class SaplMqttConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    StaticPolicyInformationPointSupplier mqttPolicyInformationPointSupplier(Clock clock) {
        return () -> List.of(MqttPolicyInformationPoint.class);
    }
}
