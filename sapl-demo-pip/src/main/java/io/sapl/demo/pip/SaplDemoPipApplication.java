package io.sapl.demo.pip;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.Relation;
import io.sapl.demo.domain.User;
import io.sapl.demo.repository.PatientenRepo;
import io.sapl.demo.repository.RelationRepo;
import io.sapl.demo.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories("io.sapl.demo.repository")
@EntityScan({ "io.sapl.demo.domain", "io.sapl.demo.repository" })
public class SaplDemoPipApplication {
	
	private static final String HRN1 = "123456";
	private static final String HRN2 = "4711";
	private static final String ROLE_DOCTOR = "DOCTOR";
	private static final String ROLE_NURSE = "NURSE";
	private static final String ROLE_VISITOR = "VISITOR";
	private static final String ROLE_ADMIN = "ADMIN";
	private static final String NAME_DOMINIK = "Dominik";
	private static final String NAME_JULIA = "Julia";
	private static final String NAME_PETER = "Peter";
	private static final String NAME_ALINA = "Alina";
	private static final String NAME_THOMAS = "Thomas";
	private static final String NAME_BRIGITTE = "Brigitte";
	private static final String NAME_JANOSCH = "Janosch";
	private static final String NAME_JANINA = "Janina";
	private static final String NAME_LENNY = "Lenny";
	private static final String NAME_KARL = "Karl";
	private static final String NAME_HORST = "Horst";

	@Value("${encrypted.testpwd}")
	private String defaultPassword;

	public static void main(String[] args) {
		SpringApplication.run(SaplDemoPipApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner demoData(UserRepo userRepo, PatientenRepo personsRepo, RelationRepo relationRepo,
			ApplicationContext ctx) {
		return args -> {
			userRepo.save(
					new User(NAME_DOMINIK, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_VISITOR))));
			userRepo.save(
					new User(NAME_JULIA, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_DOCTOR))));
			userRepo.save(
					new User(NAME_PETER, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_DOCTOR))));
			userRepo.save(
					new User(NAME_ALINA, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_DOCTOR))));
			userRepo.save(
					new User(NAME_THOMAS, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_NURSE))));
			userRepo.save(
					new User(NAME_BRIGITTE, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_NURSE))));
			userRepo.save(
					new User(NAME_JANOSCH, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_NURSE))));
			userRepo.save(
					new User(NAME_JANINA, defaultPassword, false, new ArrayList<String>(Arrays.asList(ROLE_NURSE))));

			userRepo.save(new User(NAME_HORST, defaultPassword, false,
					new ArrayList<String>(Arrays.asList(ROLE_DOCTOR, ROLE_ADMIN))));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("initialized userRepo with this Users:");
				userRepo.findAll().forEach(user -> LOGGER.debug("{}", user));
			}

			personsRepo.save(new Patient(NAME_LENNY, "sick from working", HRN1, "111111111111", NAME_JULIA, NAME_THOMAS,
					"H264"));
			personsRepo.save(new Patient(NAME_KARL, "healthy", HRN2, "222222222222", NAME_ALINA, NAME_JANINA, "N333"));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("initialized personsRepo with this Users:");
				personsRepo.findAll().forEach(person -> LOGGER.debug("{}", person));
			}

			relationRepo.save(new Relation(NAME_DOMINIK, personsRepo.findByName(NAME_LENNY).getId()));
			relationRepo.save(new Relation(NAME_JULIA, personsRepo.findByName(NAME_KARL).getId()));
			relationRepo.save(new Relation(NAME_ALINA, personsRepo.findByName(NAME_KARL).getId()));
			relationRepo.save(new Relation(NAME_JANOSCH, personsRepo.findByName(NAME_KARL).getId()));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("initialized relationRepo with this Relations:");
				relationRepo.findAll().forEach(relation -> LOGGER.debug("{}", relation));
			}
		};
	}
}
