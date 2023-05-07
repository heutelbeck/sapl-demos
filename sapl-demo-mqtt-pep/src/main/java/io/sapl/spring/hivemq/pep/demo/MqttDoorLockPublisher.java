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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Service
public class MqttDoorLockPublisher implements DisposableBean {

	Disposable publisher;

	public MqttDoorLockPublisher(MqttClientService mqttClient) {
		log.debug("Starting to send door lock status events...");
		publisher = Flux.just("open", "closed")
				.delayElements(Duration.ofSeconds(5))
				.repeat()
				.startWith("closed")
				.flatMap(status -> mqttClient.publish("door_lock_status/main", status, true)).subscribe();
	}

	@Override
	public void destroy() {
		log.debug("Stop sending door lock status events...");
		publisher.dispose();
	}
}
