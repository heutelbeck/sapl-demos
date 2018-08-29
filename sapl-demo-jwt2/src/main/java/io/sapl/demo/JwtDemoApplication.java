package io.sapl.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.sapl.demo.domain.DemoData;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.domain.RelationRepo;
import io.sapl.demo.domain.UserRepo;

@SpringBootApplication
public class JwtDemoApplication {

	/**
	 * Startup the application.
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(JwtDemoApplication.class, args);
	}

	/**
	 * Initializes the demo database with contents as defined in the demo domain.
	 *
	 * @param userRepo
	 *            the user repository
	 * @param patientRepo
	 *            the patient repository
	 * @param relationRepo
	 *            the relation repository
	 * @return initialization routine
	 */
	@Bean
	public CommandLineRunner demoData(UserRepo userRepo, PatientRepo patientRepo, RelationRepo relationRepo) {
		return args -> DemoData.loadDemoDataset(userRepo, patientRepo, relationRepo);
	}
}
