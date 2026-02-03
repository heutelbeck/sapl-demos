package io.sapl.demo.mqtt;

import com.hivemq.configuration.service.InternalConfigurations;
import com.hivemq.embedded.EmbeddedHiveMQ;
import com.hivemq.migration.meta.PersistenceType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

    Path           configDir;
    Path           dataDir;
    Path           extensionsDir;
    EmbeddedHiveMQ mqttBroker;

    public MqttBrokerLifecycleService() throws IOException, InterruptedException, ExecutionException {
        configDir     = newTemporaryDirectory();
        dataDir       = newTemporaryDirectory();
        extensionsDir = newTemporaryDirectory();
        mqttBroker    = buildBroker(configDir, dataDir, extensionsDir);
        mqttBroker.start().get(); // Block till up
        log.debug("Started HivwMQ Broker");
        log.debug("configDir    : {}", configDir.toAbsolutePath());
        log.debug("dataDir      : {}", dataDir.toAbsolutePath());
        log.debug("extensionsDir: {}", extensionsDir.toAbsolutePath());
    }

    private Path newTemporaryDirectory() throws IOException {
        return Files.createTempDirectory("saplMqttDemo");
    }

    private static EmbeddedHiveMQ buildBroker(Path configDir, Path dataDir, Path extensionsDir) {
        val broker = EmbeddedHiveMQ.builder().withConfigurationFolder(configDir).withDataFolder(dataDir)
                .withExtensionsFolder(extensionsDir).build();
        InternalConfigurations.PAYLOAD_PERSISTENCE_TYPE.set(PersistenceType.FILE);
        InternalConfigurations.RETAINED_MESSAGE_PERSISTENCE_TYPE.set(PersistenceType.FILE);
        return broker;
    }

    @Override
    public void destroy() throws InterruptedException, ExecutionException, IOException {
        mqttBroker.stop().get(); // Block till down
        log.debug("Deleting temporary directrories of broker ...");
        FileUtils.deleteDirectory(configDir.toFile());
        FileUtils.deleteDirectory(dataDir.toFile());
        FileUtils.deleteDirectory(extensionsDir.toFile());
        log.debug("Deleting temporary directrories of broker ... done");
    }

}
