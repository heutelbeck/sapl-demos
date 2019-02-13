package io.sapl.demo.pipserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.sapl.demo.domain.DemoData;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.domain.RelationRepo;
import io.sapl.demo.domain.UserRepo;
import io.sapl.springboot.autoconfig.PDPAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, PDPAutoConfiguration.class})
@EnableJpaRepositories("io.sapl.demo.domain")
@EntityScan("io.sapl.demo.domain")
@ComponentScan({"io.sapl.demo.pipserver", "io.sapl.demo.shared.pip"})
public class PipServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PipServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(UserRepo userRepo, PatientRepo patientRepo, RelationRepo relationRepo) {
        return args -> DemoData.loadDemoDataset(userRepo, "", patientRepo, relationRepo);
    }
}
