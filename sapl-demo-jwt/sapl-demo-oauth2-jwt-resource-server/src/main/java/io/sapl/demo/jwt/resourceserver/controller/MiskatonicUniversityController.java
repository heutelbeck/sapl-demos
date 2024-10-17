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
package io.sapl.demo.jwt.resourceserver.controller;

import java.security.Principal;
import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.spring.method.metadata.PreEnforce;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

@RestController
@RequiredArgsConstructor
public class MiskatonicUniversityController {

    private final PolicyDecisionPoint pdp;

    private final ObjectMapper mapper;

    @GetMapping("/books")
    @PreEnforce(action = "'read'", resource = "'books'")
    public String[] books(Principal principal) {

        /*
         * Uncomment to following line to see how a decision may change dynamically once
         * the token expires:
         */

        // @formatter:off
		return new String[] {	"Necronomicon",
								"Nameless Cults",
								"Book of Eibon"	};
		// @formatter:on
    }

    public void doATimeoutDemoTest(Principal principal) {
        final var authzSub            = AuthorizationSubscription.of(principal, "subscribe", "mysteries", mapper);
        final var decisions           = pdp.decide(authzSub).map(AuthorizationDecision::getDecision);
        final var ticktock            = Flux.just("tick", "tock").repeat().delayElements(Duration.ofSeconds(3L));
        final var decisionsWithTicker = Flux.combineLatest(x -> Tuples.of(x[0], x[1]), ticktock, decisions);
        decisionsWithTicker.subscribe();
    }

    @GetMapping("/faculty")
    @PreEnforce(action = "'read'", resource = "'faculty'")
    public String[] getMessages() {
        // @formatter:off
		return new String[] { 	"Dr. Henry Armitage",
								"Professor Ferdinand C. Ashley",
								"Professor Atwood",
								"Professor Dexter",
								"Professor William Dyer",
								"Professor Ellery",
								"Professor Tyler M. Freeborn",
								"Dr. Allen Halsey",
								"Professor Lake",
								"Dr. Francis Morgan",
								"Professor Frank H. Pabodie",
								"Professor Nathaniel Wingate Peaslee",
								"Professor Wingate Peaslee",
								"Professor Warren Rice",
								"Professor Upham",
								"\"Old\" Waldron",
								"Albert N. Wilmarth" };
		// @formatter:on
    }

    @GetMapping("/bestiary")
    @PreEnforce(action = "'read'", resource = "'bestiary'")
    public String[] getBestiary() {
        // @formatter:off
		return new String[] { 	"Byakhee",
								"Deep Ones",
								"Elder Things",
								"Ghouls",
								"Gugs",
								"Hounds of Tindalos",
								"Men of Leng",
								"Mi-Go",
								"Moon-beasts",
								"Nightgaunts",
								"Serpent Men",
								"Shoggoths" };
		// @formatter:on
    }

}
