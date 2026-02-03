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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static io.sapl.spring.method.reactive.RecoverableFluxes.recoverWith;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DemoController {

    private static final String ACCESS_DENIED_MESSAGE = "Access Denied: The data stream is temporarily suspended. "
            + "Stream will recover upon a MQTT event stating that the system is in 'emergency' state again.";

    private final DemoService       service;

    @GetMapping(value = "/secured", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ServerSentEvent<String>> recoverAfterDeny() {
        return recoverWith(service.getFluxStringRecoverable(),
                error -> log.info("Access denied: {}", error.getMessage()),
                () -> ACCESS_DENIED_MESSAGE)
                .map(value -> ServerSentEvent.<String>builder().data(value).build());
    }

}
