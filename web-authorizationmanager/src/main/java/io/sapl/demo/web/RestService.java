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
package io.sapl.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestService {

    @GetMapping("public")
    public String publicService() {
        return "public information";
    }

    @GetMapping("secret")
    public String secretService() {
        return "secret information";
    }

    /**
     * Echoes the {@code X-Correlation-Id} header back in the body. The SAPL
     * policy injects this header on permit through an
     * {@code HttpRequestMutationSignal} obligation, so the controller's view
     * of the header is what the client observes coming back.
     */
    @GetMapping("echo-correlation")
    public String echoCorrelation(@RequestHeader(name = "X-Correlation-Id", required = false) String id) {
        return id == null ? "no correlation id" : id;
    }

    /**
     * Always denied by policy with an obligation that shapes the deny
     * response into HTTP 418 with a custom body, demonstrating
     * {@code HttpDenialSignal} customisation.
     */
    @GetMapping("teapot")
    public String teapot() {
        return "this should never be reached";
    }

}
