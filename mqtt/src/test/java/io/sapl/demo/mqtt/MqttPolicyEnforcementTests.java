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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.Disposable;

/**
 * Tests demonstrating ASBAC (Attribute Stream-Based Access Control) with MQTT
 * as a Policy Information Point.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "demo.mqtt.auto-publish=false")
class MqttPolicyEnforcementTests {

    private static final String ACCESS_DENIED_MARKER = "Access Denied";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @LocalServerPort
    int port;

    @Autowired
    MqttClientService mqttClient;

    @Test
    void whenMqttStatusChanges_thenAuthorizationDecisionChangesAccordingly() {
        List<String> events = new CopyOnWriteArrayList<>();
        var sawData = new AtomicBoolean(false);
        var sawDenied = new AtomicBoolean(false);
        var sawRecovery = new AtomicBoolean(false);

        // Set initial state to "emergency" (permit)
        publishStatus("emergency");

        // Subscribe to SSE stream
        WebClient client = WebClient.create("http://localhost:" + port);
        Disposable subscription = client.get()
                .uri("/secured")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(events::add)
                .subscribe();

        try {
            // Phase 1: Wait for data events during "emergency"
            await().atMost(TIMEOUT).untilAsserted(() -> {
                assertThat(events.stream().anyMatch(e -> !e.contains(ACCESS_DENIED_MARKER))).isTrue();
            });
            sawData.set(true);
            int phase1Count = events.size();

            // Phase 2: Change to "ok" (deny) and wait for ACCESS_DENIED
            publishStatus("ok");
            await().atMost(TIMEOUT).untilAsserted(() -> {
                assertThat(events.stream().skip(phase1Count).anyMatch(e -> e.contains(ACCESS_DENIED_MARKER))).isTrue();
            });
            sawDenied.set(true);
            int phase2Count = events.size();

            // Phase 3: Change back to "emergency" (permit) and wait for recovery
            publishStatus("emergency");
            await().atMost(TIMEOUT).untilAsserted(() -> {
                assertThat(events.stream().skip(phase2Count).anyMatch(e -> !e.contains(ACCESS_DENIED_MARKER))).isTrue();
            });
            sawRecovery.set(true);

        } finally {
            subscription.dispose();
        }

        assertThat(sawData.get()).as("Data during emergency").isTrue();
        assertThat(sawDenied.get()).as("Denied during ok").isTrue();
        assertThat(sawRecovery.get()).as("Recovery after emergency").isTrue();
    }

    private void publishStatus(String status) {
        mqttClient.publish("status", status, true).block(Duration.ofSeconds(5));
    }
}
