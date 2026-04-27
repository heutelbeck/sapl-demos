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
package io.sapl.demo.webflux;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class DemoController {

    @GetMapping(value = "/public", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> publicData() {
        return Mono.just("Public information");
    }

    @GetMapping(value = "/secret", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> secretData() {
        return Mono.just("Secret information");
    }

    /**
     * Echoes the {@code X-Correlation-Id} header back. The SAPL policy
     * injects this header on permit through an
     * {@code HttpRequestMutationSignal} obligation, so the body always reads
     * {@code demo-correlation-id} regardless of what the client sent.
     */
    @GetMapping(value = "/echo-correlation", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> echoCorrelation(@RequestHeader(name = "X-Correlation-Id", required = false) String id) {
        return Mono.just(id == null ? "no correlation id" : id);
    }

    /**
     * Always denied by policy with an obligation that shapes the deny
     * response into HTTP 418 with a custom body, demonstrating
     * {@code HttpDenialSignal} customisation.
     */
    @GetMapping(value = "/teapot", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> teapot() {
        return Mono.just("this should never be reached");
    }

}
