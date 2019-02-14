package org.demo;

import org.demo.domain.DemoData;
import org.demo.domain.PatientRepo;
import org.demo.domain.RelationRepo;
import org.demo.domain.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SaplDemoPeEmbeddedApplication {

	@Value("${encrypted.testpwd}")
	private String defaultPassword;

	public static void main(String[] args) {
		SpringApplication.run(SaplDemoPeEmbeddedApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepo userRepo, PatientRepo patientRepo, RelationRepo relationRepo) {
		return args -> DemoData.loadDemoDataset(userRepo, defaultPassword, patientRepo, relationRepo);
	}

}
