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
package io.sapl.demo.mqtt;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import io.sapl.spring.method.metadata.EnforceRecoverableIfDenied;
import reactor.core.publisher.Flux;

@Service
public class DemoService {

	@EnforceRecoverableIfDenied(subject = "authentication.getName()", action = "'read'", resource = "'time'")
	public Flux<String> getFluxStringRecoverable() {
		return Flux.interval(Duration.ofMillis(500L))
				.map(i -> String.format("event after subscription %d - time %s", i, Instant.now()));
	}

}
