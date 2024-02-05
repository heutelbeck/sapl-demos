package io.sapl.springdatar2dbcdemo;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;


@SpringBootApplication(scanBasePackages = {"io.sapl.springdatar2dbcdemo", "io.sapl.springdatar2dbc"}, exclude = { SecurityAutoConfiguration.class })
public class SaplSpringDataR2dbcDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaplSpringDataR2dbcDemoApplication.class, args);
    }

    @Bean
    ConnectionFactoryInitializer initializeDatabase(ConnectionFactory connectionFactory) {

        var initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("init_scripts/schema.sql")));

        return initializer;
    }

}
