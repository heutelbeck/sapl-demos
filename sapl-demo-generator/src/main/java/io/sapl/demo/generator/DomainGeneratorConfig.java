package io.sapl.demo.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DomainGeneratorConfig {

    @Value("${sapl.policy-directory-path:#{ systemProperties['\"user.home']+'/policies'}")
    private String policyDirectoryPath;

    @Value("${sapl.count.general.roles:2")
    private int numberOfGeneralRoles;

    @Value("${sapl.count.general.resources:2")
    private int numberOfGeneralResources;

    @Value("${sapl.count.departments:5")
    private int numberOfDepartments;


    @Bean
    public DomainData domainParameter() {
        return new DomainData(numberOfGeneralRoles, numberOfGeneralResources, numberOfDepartments);
    }


    @Bean
    public GeneratorUtility generatorUtility() {
        LOGGER.info("Policy directory path: {}", policyDirectoryPath);
        return new GeneratorUtility(policyDirectoryPath);
    }
}
