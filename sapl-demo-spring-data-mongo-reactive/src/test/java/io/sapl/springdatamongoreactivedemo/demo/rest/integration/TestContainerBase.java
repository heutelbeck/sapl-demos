package io.sapl.springdatamongoreactivedemo.demo.rest.integration;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@SpringBootTest
@Tag("integration-test")
public class TestContainerBase {

    public static final GenericContainer<?> MONGO_DB_CONTAINER = new GenericContainer<>(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017)
            .withCopyFileToContainer(MountableFile.forClasspathResource("./mongo-init.js"), "/docker-entrypoint-initdb.d/mongo-init.js")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "root")
            .withEnv("MONGO_INITDB_DATABASE", "sapl4db");

    static {
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", MONGO_DB_CONTAINER::getHost);
        registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);
        registry.add("spring.data.mongodb.authentication-database", () -> "admin");
        registry.add("spring.data.mongodb.username", () -> "root");
        registry.add("spring.data.mongodb.password", () -> "root");
        registry.add("spring.data.mongodb.database", () -> "sapl4db");
    }
}

