package io.sapl.demo.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DomainGeneratorApplication implements CommandLineRunner {

    @Autowired
    private DomainGenerator domainGenerator;


    public static void main(String[] args) {
        LOGGER.info("Starting DomainGeneratorApplication");
        SpringApplication.run(DomainGeneratorApplication.class, args);
    }


    @Override
    public void run(String... args) {
        LOGGER.info("Generating Domain");
        domainGenerator.generateDomainPolicies();
    }
}
