/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.demo.webflux;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class DemoController {
	private final static JsonNodeFactory JSON = JsonNodeFactory.instance;
	
	@GetMapping(value = "/numbers", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<ServerSentEvent<JsonNode>> numbers() {
		return Flux.range(0, 10).repeat().delayElements(Duration.ofMillis(500L))
				.map(value -> ServerSentEvent.<JsonNode>builder().data(JSON.numberNode(value)).build());
	}

	@GetMapping(value = "/public", produces = MediaType.TEXT_PLAIN_VALUE)
	public Mono<String> publicData() {
		return Mono.just("Public information");
	}

	@GetMapping(value = "/secret", produces = MediaType.TEXT_PLAIN_VALUE)
	public Mono<String> secretData() {
		return Mono.just("Secret information");
	}
}
