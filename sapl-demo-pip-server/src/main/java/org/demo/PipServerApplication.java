package org.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EntityScan
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PipServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PipServerApplication.class, args);
    }
}
