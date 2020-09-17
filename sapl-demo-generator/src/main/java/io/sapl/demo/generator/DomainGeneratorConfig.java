package io.sapl.demo.generator;

import io.sapl.demo.generator.example.ExampleProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

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
    public DomainParameter domainParameter() {
          return DomainParameter.builder()
                  .numberOfGeneralRoles(2)
                  .numberOfGeneralResources(2)
                  .numberOfDepartments(5)
                  .build();
    }

    @Bean
    @DependsOn("domainParameter")
    public ExampleProvider domainRoleProvider(DomainParameter domainParameter) {
        return new ExampleProvider(domainParameter);
    }

    @Bean
    public GeneratorUtility generatorUtility() {
        LOGGER.info("Policy directory path: {}", policyDirectoryPath);
        return new GeneratorUtility(policyDirectoryPath);
    }
}
