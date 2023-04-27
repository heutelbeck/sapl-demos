package io.sapl.demo.mqtt;

import java.time.Duration;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class MqttDataPublisher implements DisposableBean {

	Disposable publisher;

	public MqttDataPublisher(MqttClientService mqttClient) {
		log.debug("Starting to send status events...");
		publisher = Flux.interval(Duration.ofSeconds(3L)).map(i -> i % 2 == 0 ? "ok" : "emergency")
				.flatMap(status -> mqttClient.publish("status", status, true)).subscribe();
	}

	@Override
	public void destroy() throws Exception {
		log.debug("Stop sending status events...");
		publisher.dispose();
	}

}
