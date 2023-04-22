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

package io.sapl.mqtt.pip.demo.helper;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.EnvironmentAttribute;
import io.sapl.api.pip.PolicyInformationPoint;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * This pip provides toggle functionality for demonstration purposes.
 */
@PolicyInformationPoint(name = MqttTogglePip.NAME, description = MqttTogglePip.DESCRIPTION)
public class MqttTogglePip {

    static final String NAME = "mqttToggle";
    static final String DESCRIPTION = "This pip only toggles some client ids for demonstration purposes.";

    private static final String CLIENT_ID_FIRST = "mqttPip2";
    private static final String CLIENT_ID_SECOND = "mqttPip3";

    /**
     * This attribute finder toggles different client ids.
     * @return returns an alternating client id
     */
    @EnvironmentAttribute(name = "toggleId", docs = "Periodically switches between two different client ids.")
    public Flux<Val> toggleId() {
        return Flux.just(CLIENT_ID_FIRST, CLIENT_ID_SECOND)
                .repeat()
                .delayElements(Duration.ofMillis(5000))
                .startWith(CLIENT_ID_SECOND)
                .map(Val::of);
    }
}
