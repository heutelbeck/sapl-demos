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

package io.sapl.spring.hivemq.pep.demo;

import com.hivemq.configuration.service.InternalConfigurations;
import com.hivemq.embedded.EmbeddedExtension;
import com.hivemq.embedded.EmbeddedHiveMQ;
import com.hivemq.migration.meta.PersistenceType;
import io.sapl.mqtt.pep.HivemqPepExtensionMain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class MqttBrokerLifecycleService implements DisposableBean {

	Path           		configDir;
	Path           		dataDir;
	Path           		extensionsDir;
	EmbeddedHiveMQ 		mqttBroker;

	static final String EXTENSIONS_PATH = "src/main/resources";
	static final String POLICIES_PATH 	= "src/main/resources/policies";

	public MqttBrokerLifecycleService() throws IOException, InterruptedException, ExecutionException {
		configDir     = newTemporaryDirectory();
		dataDir       = newTemporaryDirectory();
		extensionsDir = newTemporaryDirectory();
		mqttBroker    = buildBroker(configDir, dataDir, extensionsDir);
		mqttBroker.start().get(); // Block till up
		log.debug("Started HiveMQ Broker");
		log.debug("configDir    : {}", configDir.toAbsolutePath());
		log.debug("dataDir      : {}", dataDir.toAbsolutePath());
		log.debug("extensionsDir: {}", extensionsDir.toAbsolutePath());
	}

	private Path newTemporaryDirectory() throws IOException {
		return Files.createTempDirectory("saplMqttDemo");
	}

	private static EmbeddedHiveMQ buildBroker(Path configDir, Path dataDir, Path extensionsDir) {
		var broker = EmbeddedHiveMQ.builder()
				.withConfigurationFolder(configDir)
				.withDataFolder(dataDir)
				.withExtensionsFolder(extensionsDir)
				.withEmbeddedExtension(buildEmbeddedSaplMqttPepExtension())
				.build();
		InternalConfigurations.PAYLOAD_PERSISTENCE_TYPE.set(PersistenceType.FILE);
		InternalConfigurations.RETAINED_MESSAGE_PERSISTENCE_TYPE.set(PersistenceType.FILE);
		return broker;
	}

	private static EmbeddedExtension buildEmbeddedSaplMqttPepExtension() {
		return EmbeddedExtension.builder()
				.withId("SAPL-HIVEMQ-EXTENSION")
				.withName("SAPL-HIVEMQ-EXTENSION")
				.withVersion("3.0.0-SNAPSHOT")
				.withPriority(0)
				.withStartPriority(1000)
				.withAuthor("Nils Mahnken")
				.withExtensionMain(new HivemqPepExtensionMain(POLICIES_PATH, EXTENSIONS_PATH))
				.build();
	}

	@Override
	public void destroy() throws InterruptedException, ExecutionException, IOException  {
		mqttBroker.stop().get(); // Block till down
		log.debug("Deleting temporary directories of broker ...");
		FileUtils.deleteDirectory(configDir.toFile());
		FileUtils.deleteDirectory(dataDir.toFile());
		FileUtils.deleteDirectory(extensionsDir.toFile());
		log.debug("Deleting temporary directories of broker ... done");
	}
}
