package io.sapl.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.sapl.demo.domain.DemoData;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.domain.RelationRepo;
import io.sapl.demo.domain.UserRepo;

@SpringBootApplication
public class SaplDemoPeEmbeddedApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaplDemoPeEmbeddedApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepo userRepo, PatientRepo patientRepo, RelationRepo relationRepo,
			ApplicationContext ctx) {
		return args -> DemoData.loadDemoDataset(userRepo, patientRepo, relationRepo);
	}

}
