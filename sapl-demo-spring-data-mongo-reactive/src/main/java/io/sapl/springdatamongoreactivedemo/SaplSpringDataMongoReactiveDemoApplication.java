package io.sapl.springdatamongoreactivedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"io.sapl.springdatamongoreactivedemo", "io.sapl.springdatamongoreactive"}, exclude = { SecurityAutoConfiguration.class })
public class SaplSpringDataMongoReactiveDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaplSpringDataMongoReactiveDemoApplication.class, args);
    }

}
