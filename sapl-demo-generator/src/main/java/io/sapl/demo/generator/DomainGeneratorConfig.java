package io.sapl.demo.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

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

    @Value("${sapl.probability.department.additional.roles:0.0")
    private double probabilityOfAdditionalRoles;

    @Value("${apl.probability.department.additional.resources:0.0")
    private double probabilityOfAdditionalResources;


    @PostConstruct
    private void logSettings() {
        LOGGER.info("DomainData{" +
                "numberOfGeneralRoles=" + numberOfGeneralRoles +
                ", numberOfGeneralResources=" + numberOfGeneralResources +
                ", numberOfDepartments=" + numberOfDepartments +
                ", probabilityOfAdditionalRoles=" + probabilityOfAdditionalRoles +
                ", probabilityOfAdditionalResources=" + probabilityOfAdditionalResources +
                '}');
    }

    @Bean
    public DomainData domainParameter() {
        return new DomainData(numberOfGeneralRoles, numberOfGeneralResources, numberOfDepartments,
                probabilityOfAdditionalRoles, probabilityOfAdditionalResources);
    }


    @Bean
    public DomainUtil generatorUtility() {
        LOGGER.info("Policy directory path: {}", policyDirectoryPath);
        return new DomainUtil(policyDirectoryPath);
    }
}
