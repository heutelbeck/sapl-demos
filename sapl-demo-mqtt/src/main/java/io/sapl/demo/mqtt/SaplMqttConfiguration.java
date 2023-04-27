package io.sapl.demo.mqtt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sapl.extensions.mqtt.MqttPolicyInformationPoint;

@Configuration
public class SaplMqttConfiguration {

	@Bean
	MqttPolicyInformationPoint mqttPolicyInformationPoint() {
		return new MqttPolicyInformationPoint();
	}

}
