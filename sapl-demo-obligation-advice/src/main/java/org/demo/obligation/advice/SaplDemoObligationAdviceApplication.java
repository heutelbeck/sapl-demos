package org.demo.obligation.advice;

import org.demo.domain.DemoData;
import org.demo.domain.PatientRepository;
import org.demo.domain.RelationRepository;
import org.demo.domain.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.demo.domain")
@EntityScan("org.demo.domain")
public class SaplDemoObligationAdviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaplDemoObligationAdviceApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepository userRepo, PatientRepository patientRepo, RelationRepository relationRepo) {
		return args -> DemoData.loadDemoDataset(userRepo, patientRepo, relationRepo);
	}
}
