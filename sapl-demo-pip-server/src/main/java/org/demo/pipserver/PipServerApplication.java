package org.demo.pipserver;

import org.demo.domain.DemoData;
import org.demo.domain.PatientRepo;
import org.demo.domain.RelationRepo;
import org.demo.domain.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableJpaRepositories("org.demo.domain")
@EntityScan("org.demo.domain")
@ComponentScan({"org.demo.pipserver", "org.demo.shared.pip"})
public class PipServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PipServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(UserRepo userRepo, PatientRepo patientRepo, RelationRepo relationRepo) {
        return args -> DemoData.loadDemoDataset(userRepo, "", patientRepo, relationRepo);
    }
}
