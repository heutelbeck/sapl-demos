package io.sapl.generator;

import io.sapl.generator.DomainRole.DomainRoles;
import io.sapl.generator.DomainRole.ExtendedDomainRole;
import io.sapl.generator.example.Department;
import io.sapl.generator.example.ExampleProvider;
import io.sapl.generator.example.Hospital;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Slf4j
@Configuration
public class DomainData {

    @Value("${sapl.policy-directory.path:#systemProperties['\"user.home']+'/policies'}")
    private String policyDirectoryPath;

    @Value("${sapl.policy-directory.clean-on-start:true}")
    private boolean cleanPolicyDirectory;

    @Value("${sapl.random.seed:2454325}")
    private long seed;

    //#### DOMAIN ####
    @Value("${sapl.number.of.subjects}")
    private int numberOfSubjects;
    @Value("${sapl.maximum.of.subject.roles}")
    private int limitOfSubjectRoles;
    @Value("${sapl.number.of.subjects.locked}")
    private int numberOfLockedSubjects;
    @Value("${sapl.maximum.additional.entries}")
    private int limitOfAdditional;

    //actions
    @Value("${sapl.number.of.actions}")
    private int numberOfActions;
    @Value("${sapl.probability.of.additional.actions}")
    private double probabilityOfAdditionalActions;

    //resources
    @Value("${sapl.number.of.resources}")
    private int numberOfGeneralResources;
    @Value("${sapl.probability.of.extended.resource}")
    private double probabilityOfExtendedResource;
    @Value("${sapl.probability.of.additional.resource}")
    private double probabilityOfAdditionalResources;
    @Value("${sapl.probability.of.unrestricted.resource}")
    private double probabilityOfUnrestrictedResource; // all have full access

    //roles
    @Value("${sapl.number.of.roles}")
    private int numberOfGeneralRoles;
    @Value("${sapl.probability.of.extended.role}")
    private double probabilityOfExtendedRole;
    @Value("${sapl.probability.of.additional.role}")
    private double probabilityOfAdditionalRoles;
    @Value("${sapl.probability.of.full.access.role}")
    private double probabilityOfGeneralFullAccessRole; // GERING! 1/17 => Full ACCESS ROLES haben keine weiteren Policies
    @Value("${sapl.probability.of.read.access.role}")
    private double probabilityOfGeneralReadAccessRole; // ETWAS HÖHER! 3/17
    @Value("${sapl.probability.of.custom.access.role}")
    private double probabilityOfGeneralCustomAccessRole; // GERING! 1/17 => NUr diese Rolle hat genau diesen Zugriffstyp

    //per resource & role
    @Value("${sapl.probability.of.full.access.on.resource}")
    private double probabilityFullAccessOnResource;
    @Value("${sapl.probability.of.read.access.on.resource}")
    private double probabilityReadAccessOnResource;
    @Value("${sapl.probability.of.custom.access.on.resource}")
    private double probabilityCustomAccessOnResource;

    //#### DEPARTMENT ####
    @Value("${sapl.number.of.departments}")
    private int numberOfDepartments;
    private int numberOfAdditionalRoles;
    private int numberOfAdditionalResources;
    private int numberOfAdditionalActions;

    //AuthorizationSubscription Generation
    @Value("${sapl.subscription.generation.factor}")
    private int subscriptionGenerationFactor;
    @Value("${sapl.number.of.benchmark.runs}")
    private int numberOfBenchmarkRuns;

    private int numberOfGeneratedSubscriptions;

    @Value("${sapl.probability.empty.subscription:0.8}")
    private double probabilityEmptySubscription;
    @Value("${sapl.probability.empty.subscription.node:0.4}")
    private double probabilityEmptySubscriptionNode;


    private Hospital hospital;
    private List<Department> departments = new ArrayList<>();

    private List<DomainRole> hospitalRoles;
    private List<DomainResource> hospitalResources;

    private Random dice;

    private List<DomainRole> domainRoles = new LinkedList<>();
    private List<DomainResource> domainResources = new LinkedList<>();
    private List<DomainSubject> domainSubjects = new LinkedList<>();
    private List<String> domainActions = new LinkedList<>();
    private DomainUtil domainUtil;


    public List<DomainRole> getDomainRoles() {
        return List.copyOf(domainRoles);
    }

    public List<DomainResource> getDomainResources() {
        return List.copyOf(domainResources);
    }

    public List<DomainSubject> getDomainSubjects() {
        return List.copyOf(domainSubjects);
    }

    public List<String> getDomainActions() {
        return List.copyOf(domainActions);
    }

    private int getAdditionalCount(boolean rollCount) {
        return rollCount ? dice.nextInt(limitOfAdditional) + 1 : 0;
    }

    public Random initDiceWithSeed(long newSeed) {
        this.seed = newSeed;
        return dice();
    }

    @Bean
    public Random dice() {
        this.dice = new Random(seed);

        this.numberOfGeneralRoles = Math.max(numberOfGeneralRoles, ExampleProvider.EXAMPLE_MANDATORY_ROLE_LIST.size());
        this.numberOfGeneralResources =
                Math.max(numberOfGeneralResources, ExampleProvider.EXAMPLE_MANDATORY_RESOURCE_LIST.size());

        this.numberOfAdditionalRoles = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalRoles);
        this.numberOfAdditionalResources = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalResources);
        this.numberOfAdditionalActions = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalActions);

        this.numberOfGeneratedSubscriptions = this.numberOfBenchmarkRuns * this.subscriptionGenerationFactor + 100;

        return this.dice;
    }

    public double roll() {
        return getDice().nextDouble();
    }

    public boolean rollIsLowerThanProbability(double probability) {
        return roll() < probability;
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(getDice().nextInt(list.size()));
    }

    @Bean
    @DependsOn("dice")
    public DomainUtil generatorUtility() {
        this.domainUtil = new DomainUtil(cleanPolicyDirectory);

        this.domainRoles = generateRoles();
        this.domainResources = generateResources();
        this.domainSubjects = generateSubjects(this.domainRoles);
        this.domainActions = DomainActions.generateActionListByCount(this.getNumberOfActions());

        LOGGER.debug("generated {} roles", this.domainRoles.size());
        LOGGER.debug("generated {} resources", this.domainResources.size());
        LOGGER.debug("generated {} subjects", this.domainSubjects.size());
        LOGGER.debug("generated {} actions", this.domainActions.size());

        return this.domainUtil;
    }

    @PostConstruct
    public void init() {
        createHospital();
        createDepartments();
    }

    private void createHospital() {
        this.hospital = new Hospital("Demo Hospital GmbH", numberOfGeneralRoles, numberOfGeneralResources);
        this.hospital.init();
        this.hospitalRoles = hospital.getHospitalRoles();
        this.hospitalResources = hospital.getHospitalResources();

    }

    @DependsOn("generatorUtility")
    private List<DomainRole> generateRoles() {
        List<DomainRole> roles = new ArrayList<>();

        for (int i = 0; i < this.getNumberOfGeneralRoles(); i++) {
            roles.add(new DomainRole(String.format("role.%03d", DomainUtil.getNextRoleCount()),
                    rollIsLowerThanProbability(this.getProbabilityOfGeneralFullAccessRole()),
                    rollIsLowerThanProbability(this.getProbabilityOfGeneralReadAccessRole()),
                    rollIsLowerThanProbability(this.getProbabilityOfGeneralCustomAccessRole()),
                    rollIsLowerThanProbability(this.getProbabilityOfExtendedRole())
            ));
        }
        return roles;
    }

    @DependsOn("generatorUtility")
    private List<DomainResource> generateResources() {
        List<DomainResource> resources = new ArrayList<>();

        for (int i = 0; i < this.getNumberOfGeneralResources(); i++) {
            resources.add(new DomainResource(String.format("resource.%03d", DomainUtil.getNextResourceCount()),
                    rollIsLowerThanProbability(this.getProbabilityOfUnrestrictedResource()),
                    rollIsLowerThanProbability(this.getProbabilityOfExtendedRole())
            ));
        }
        return resources;
    }

    @DependsOn("generatorUtility")
    private List<DomainSubject> generateSubjects(List<DomainRole> allRoles) {
        List<DomainSubject> subjects = new ArrayList<>();

        for (int i = 0; i < this.getNumberOfSubjects(); i++) {
            DomainSubject domainSubject = new DomainSubject(String.format("subject.%03d", i));

            //assign subject random roles (up to 3)
            for (int j = 0; j < this.dice.nextInt(this.getLimitOfSubjectRoles()) + 1; j++) {
                DomainRole randomRole = getRandomElement(allRoles);
                domainSubject.getSubjectAuthorities().add(randomRole.getRoleName());
            }

            subjects.add(domainSubject);
        }

        return subjects;
    }


    private void createDepartments() {
        //TODO department creation
        // - create different departments
        for (int i = 0; i < numberOfDepartments; i++) {
            String departmentName;
            try {
                departmentName = ExampleProvider.EXAMPLE_DEPARTMENT_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                departmentName = "DEPARTMENT" + i;
            }

            Department department = new Department(departmentName,
                    numberOfAdditionalRoles, numberOfAdditionalResources,
                    numberOfAdditionalActions, DomainActions.CRUD, DomainActions.READ_ONLY);

            department.init();

            createDepartmentExtraRoles(department);

            this.departments.add(department);
        }
    }


    private void createDepartmentExtraRoles(Department department) {
        //PUBLIC: DIRECTOR
        department.addRolesForPublicActions(Collections.singletonList(
                DomainRoles.findByName(hospital.getHospitalRoles(), ExampleProvider.ROLE_DIRECTOR)));

        //EXTENDED ROLE: TREATING
        List<ExtendedDomainRole> extendedDomainRoles = Stream.of(
                DomainRoles.findByName(hospital.getHospitalRoles(), ExampleProvider.ROLE_DOCTOR),
                DomainRoles.findByName(hospital.getHospitalRoles(), ExampleProvider.ROLE_NURSE)
        ).map(role -> ExtendedDomainRole.builder().role(role)
                .body(DomainUtil.TREATING_BODY)
                .build()).collect(Collectors.toList());

        //EXTENDED ROLE: OWN DATA (PIP used)
        extendedDomainRoles.add(ExtendedDomainRole.builder()
                .role(DomainRoles.findByName(hospital.getHospitalRoles(), ExampleProvider.ROLE_PATIENT))
                .body(DomainUtil.OWN_DATA_BODY)
                .build()
        );

        department.addExtendedRolesForPublicActions(extendedDomainRoles);

        //SPECIAL: INTERNAL STAFF
        department.addRolesForSpecialActions(department.getDepartmentRoles());

        //SPECIAL EXTENDED: DIRECTOR (with log obligation)
        department.addExtendedRolesForSpecialActions(Collections.singletonList(
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospital.getHospitalRoles(), ExampleProvider.ROLE_DIRECTOR))
                        .obligation(DomainUtil.LOG_OBLIGATION)
                        .build()
        ));
    }

}
