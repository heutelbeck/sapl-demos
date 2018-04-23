package io.sapl.demo.obligation.advice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.sapl.demo.domain.DemoData;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.domain.RelationRepo;
import io.sapl.demo.domain.UserRepo;

@SpringBootApplication
@EnableJpaRepositories("io.sapl.demo.repository")
@EntityScan({ "io.sapl.demo.domain", "io.sapl.demo.repository" })
public class SaplDemoObligationAdviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaplDemoObligationAdviceApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepo userRepo, PatientRepo personsRepo, RelationRepo relationRepo,
			ApplicationContext ctx) {
		return args -> {
			DemoData.loadDemoDataset(userRepo, personsRepo, relationRepo);
		};
	}
}
