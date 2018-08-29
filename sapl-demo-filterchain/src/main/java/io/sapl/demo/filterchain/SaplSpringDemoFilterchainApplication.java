package io.sapl.demo.filterchain;

import io.sapl.demo.domain.DemoData;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.domain.RelationRepo;
import io.sapl.demo.domain.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("io.sapl.demo.domain")
@EntityScan("io.sapl.demo.domain")
public class SaplSpringDemoFilterchainApplication {

	@Value("${encrypted.testpwd}")
	private String defaultPassword;

	public static void main(String[] args) {
		SpringApplication.run(SaplSpringDemoFilterchainApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepo userRepo, PatientRepo patientRepo, RelationRepo relationRepo,
			ApplicationContext ctx) {
		return args -> DemoData.loadDemoDataset(userRepo, defaultPassword, patientRepo, relationRepo);
	}
}
