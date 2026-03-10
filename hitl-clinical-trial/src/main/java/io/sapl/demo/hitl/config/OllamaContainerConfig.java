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
package io.sapl.demo.hitl.config;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Binds;
import com.github.dockerjava.api.model.DeviceRequest;
import com.github.dockerjava.api.model.Volume;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Slf4j
@Profile("ollama-docker")
@Configuration(proxyBeanMethods = false)
class OllamaContainerConfig {

    private static final DockerImageName OLLAMA_IMAGE = DockerImageName.parse("ollama/ollama:latest");

    static final class OllamaContainer extends GenericContainer<OllamaContainer> {
        OllamaContainer(DockerImageName image) {
            super(image);
        }
    }

    private static final OllamaContainer EARLY_OLLAMA;

    static {
        EARLY_OLLAMA = startWithGpuFallback();
    }

    private static OllamaContainer startWithGpuFallback() {
        int containerPort = 11434;

        try {
            val container = createContainer(containerPort, true);
            container.start();
            log.info("Ollama container started with GPU support");
            configureBaseUrl(container, containerPort);
            return container;
        } catch (Exception e) {
            log.info("GPU not available ({}), starting Ollama with CPU only", e.getMessage());
            val container = createContainer(containerPort, false);
            container.start();
            log.info("Ollama container started with CPU only");
            configureBaseUrl(container, containerPort);
            return container;
        }
    }

    private static OllamaContainer createContainer(int containerPort, boolean withGpu) {
        val c = new OllamaContainer(OLLAMA_IMAGE)
                .withEnv("OLLAMA_KEEP_ALIVE", "5m")
                .withEnv("OLLAMA_MODELS", "/models")
                .withCommand("serve")
                .withExposedPorts(containerPort)
                .withCreateContainerCmdModifier(cmd -> {
                    val hostConfig = Objects.requireNonNull(cmd.getHostConfig());
                    if (withGpu) {
                        hostConfig.withDeviceRequests(List.of(new DeviceRequest()
                                .withCount(-1)
                                .withCapabilities(List.of(List.of("gpu")))));
                    }
                    hostConfig.withBinds(new Binds(new Bind("ollama-models", new Volume("/models"))));
                });

        c.setWaitStrategy(Wait.forHttp("/api/tags")
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofMinutes(3)));

        return c;
    }

    private static void configureBaseUrl(OllamaContainer container, int containerPort) {
        val mappedPort = container.getMappedPort(containerPort);
        System.setProperty("spring.ai.ollama.base-url", "http://localhost:" + mappedPort);
    }

    @Bean(destroyMethod = "stop")
    OllamaContainer ollamaContainer() {
        return EARLY_OLLAMA;
    }

}
