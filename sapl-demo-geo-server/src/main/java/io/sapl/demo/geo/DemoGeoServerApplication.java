package io.sapl.demo.geo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.sapl.demo.geo.domain.CrewMember;
import io.sapl.demo.geo.domain.CrewRepo;

@SpringBootApplication
@EntityScan("io.sapl.demo.geo.domain")
@EnableJpaRepositories("io.sapl.demo.geo.domain")
public class DemoGeoServerApplication {

	private static final String CREW1 = "a12345";
	private static final String CREW2 = "a11111";
	private static final String ROLE_CREW = "CREW";

	@Value("${encrypted.testpwd}")
	private String defaultPassword;

	public static void main(String[] args) {
		SpringApplication.run(DemoGeoServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(CrewRepo crewRepo) {
		// stub method to generate sample user data
		return args -> {
			crewRepo.save(new CrewMember(CREW1, defaultPassword, ROLE_CREW, true));
			crewRepo.save(new CrewMember(CREW2, defaultPassword, ROLE_CREW, true));
		};
	}
}
