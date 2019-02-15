package org.demo;

import org.demo.domain.DemoData;
import org.demo.domain.PatientRepository;
import org.demo.domain.RelationRepository;
import org.demo.domain.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SaplDemoVoterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaplDemoVoterApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepository userRepo, PatientRepository patientRepo,
			RelationRepository relationRepo) {
		return args -> DemoData.loadDemoDataset(userRepo, patientRepo, relationRepo);
	}

}
