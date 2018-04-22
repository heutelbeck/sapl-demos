package io.sapl.sapldemoauthorizationaspects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.sapl.demo.data.DemoData;
import io.sapl.demo.repository.PatientenRepo;
import io.sapl.demo.repository.RelationRepo;
import io.sapl.demo.repository.UserRepo;

@SpringBootApplication
@EnableJpaRepositories("io.sapl.demo.repository")
@EntityScan({ "io.sapl.demo.domain", "io.sapl.demo.repository" })
public class SaplDemoAuthorizationAspectsApplication {

	@Value("${encrypted.testpwd}")
	private String defaultPassword;

	public static void main(String[] args) {
		SpringApplication.run(SaplDemoAuthorizationAspectsApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepo userRepo, PatientenRepo personsRepo, RelationRepo relationRepo,
			ApplicationContext ctx) {
		return args -> {
			DemoData.loadDemoDataset(userRepo, personsRepo, relationRepo);
		};
	}
}
