/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.demo.mqtt;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DemoController {

    public static final JsonNodeFactory JSON = JsonNodeFactory.instance;

    private final DemoService       service;
    private final MqttClientService mqttClient;

    @GetMapping(value = "/secured", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ServerSentEvent<String>> recoverAfterDeny() {
        return service.getFluxStringRecoverable().onErrorContinue(AccessDeniedException.class,
                (error, reason) -> log.warn("ACCESS DENIED ('" + error.getMessage()
                        + "') (data will automatically resume once the MQTT topic 'status' gets an 'emergency' event."))
                .map(value -> ServerSentEvent.<String>builder().data(value).build());
    }

    @GetMapping(value = "/publishMqttEvent")
    public Mono<String> publishMqttEvent() {
        return mqttClient.publish("demoTopic", "aMessage", true).map(Object::toString);
    }

}
