package io.sapl.generator;

import io.sapl.generator.DomainResource.DomainResources;
import io.sapl.generator.DomainRole.DomainRoles;
import io.sapl.generator.DomainRole.ExtendedDomainRole;
import io.sapl.generator.example.Department;
import io.sapl.generator.example.ExampleProvider;
import io.sapl.generator.example.Hospital;
import io.sapl.generator.example.patientresources.ResourceMedication;
import io.sapl.generator.example.patientresources.ResourcePersonalDetails;
import io.sapl.generator.example.patientresources.ResourceRoom;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private Hospital hospital;
    private List<Department> departments = new ArrayList<>();

    private List<DomainRole> hospitalRoles;
    private List<DomainResource> hospitalResources;

    private Random dice;

//    @PostConstruct
//    private void postConstruct() {
//        this.numberOfGeneralRoles = Math.max(numberOfGeneralRoles, ExampleProvider.EXAMPLE_MANDATORY_ROLE_LIST.size());
//        this.numberOfGeneralResources =
//                Math.max(numberOfGeneralResources, ExampleProvider.EXAMPLE_MANDATORY_RESOURCE_LIST.size());
//
//        this.numberOfAdditionalRoles = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalRoles);
//        this.numberOfAdditionalResources = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalResources);
//        this.numberOfAdditionalActions = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalActions);
//    }

    private int getAdditionalCount(boolean rollCount) {
        return rollCount ? dice.nextInt(limitOfAdditional) + 1 : 0;
    }

    @Bean("settingsFileContent")
    public String getSettingsFileContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("#### CONFIGURATION FILE ####").append(System.lineSeparator())
                .append("\t").append(String.format("policy.output.directory: %s", policyDirectoryPath))
                .append(System.lineSeparator())
                .append("\t").append(String.format("policy.output.directory.clean: %b", cleanPolicyDirectory))
                .append(System.lineSeparator())
                .append("\t").append(String.format("general.role.count: %d", numberOfGeneralRoles))
                .append(System.lineSeparator())
                .append("\t").append(String.format("general.resource.count: %d", numberOfGeneralResources))
                .append(System.lineSeparator())
                .append("\t").append(String.format("general.department.count: %d", numberOfDepartments))
                .append(System.lineSeparator())
                .append(String.format("\tdepartment.additional.role.chance: %.2f", probabilityOfAdditionalRoles))
                .append(System.lineSeparator())
                .append(String
                        .format("\tdepartment.additional.resource.chance: %.2f", probabilityOfAdditionalResources))
                .append(System.lineSeparator())
                .append("\t").append(String.format("department.additional.role.count: %d", numberOfAdditionalRoles))
                .append(System.lineSeparator())
                .append(String.format("\tdepartment.additional.resource.count: %d", numberOfAdditionalResources));

        return sb.toString();
    }

    @Bean
    public Random dice(){
        this.dice = new Random(seed);

        this.numberOfGeneralRoles = Math.max(numberOfGeneralRoles, ExampleProvider.EXAMPLE_MANDATORY_ROLE_LIST.size());
        this.numberOfGeneralResources =
                Math.max(numberOfGeneralResources, ExampleProvider.EXAMPLE_MANDATORY_RESOURCE_LIST.size());

        this.numberOfAdditionalRoles = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalRoles);
        this.numberOfAdditionalResources = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalResources);
        this.numberOfAdditionalActions = getAdditionalCount(dice.nextDouble() < probabilityOfAdditionalActions);

        return this.dice;
    }

    @Bean
    @DependsOn("dice")
    public DomainUtil generatorUtility(Random dice) {
        return new DomainUtil(policyDirectoryPath, cleanPolicyDirectory, dice);
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


//        createHospitalGeneralAccess();
//        createHospitalResourceSpecificRoleAccess();
//        createHospitalResourceSpecificExtendedRoleAccess();
//        printHospitalStatistics();

    }

    private void printHospitalStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("#### HOSPITAL ####").append(System.lineSeparator())
                .append("\t").append(String.format("ROLES GENERAL TOTAL: %d", hospital.getHospitalRoles().size()))
                .append(System.lineSeparator())
                .append("\t")
                .append(String.format("ROLES GENERAL FULL ACCESS: %d", hospital.getRolesWithGeneralFullAccess().size()))
                .append(System.lineSeparator())
                .append("\t")
                .append(String.format("ROLES GENERAL READ ACCESS: %d", hospital.getRolesWithGeneralReadAccess().size()))
                .append(System.lineSeparator())
                .append("\t").append(String
                .format("ROLES GENERAL SPECIAL ACCESS: %d", hospital.getRolesWithGeneralSpecialAccess().size()))
                .append(System.lineSeparator())
                .append("\t").append(String
                .format("ROLES EXTENDED FULL ACCESS: %d", hospital.getExtendedRolesWithGeneralFullAccess()))
                .append(System.lineSeparator())
                .append(String
                        .format("\tROLES EXTENDED READ ACCESS: %d", hospital.getExtendedRolesWithGeneralReadAccess()));

//        return sb.toString();
    }

    private void createHospitalResourceSpecificExtendedRoleAccess() {
        Map<DomainResource, Map<ExtendedDomainRole, DomainActions>> map = hospital
                .getResourceSpecificExtendedRoleAccess();

        map.put(DomainResources.findByName(hospitalResources, ResourceRoom.NAME), Map.of(
                //Patients have full access on own personal details
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_PATIENT))
                        .body(DomainUtil.OWN_DATA_BODY)
                        .build(), DomainActions.CRUD,
                //Relatives have read access on personal details
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR))
                        .body(DomainUtil.RELATIVE_BODY)
                        .build(), DomainActions.READ_ONLY
        ));

    }

    private void createHospitalResourceSpecificRoleAccess() {
        Map<DomainResource, Map<DomainRole, DomainActions>> resourceSpecificRoleAccess = hospital
                .getResourceSpecificRoleAccess();

        //ROOM
        DomainResource roomResource = DomainResources.findByName(hospitalResources, ResourceRoom.NAME);
        resourceSpecificRoleAccess.put(roomResource, Map.of(
                //Visitors can read room information
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR), DomainActions.READ_ONLY,
                // Nurse has full access on rooms in addition to general read permission
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE), DomainActions.CRUD
        ));

        //PERSONAL DETAILS
        DomainResource personalDetailsResource = DomainResources
                .findByName(hospitalResources, ResourcePersonalDetails.NAME);
        resourceSpecificRoleAccess.put(personalDetailsResource, Map.of(
                // Nurse has full access on personal details in addition to general read permission
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE), DomainActions.CRUD
        ));

        //MEDICATION
        DomainResource medicationResource = DomainResources.findByName(hospitalResources, ResourceMedication.NAME);
        resourceSpecificRoleAccess.put(medicationResource, Map.of());

    }

    private void createHospitalGeneralAccess() {

        hospital.getRolesWithGeneralFullAccess().addAll(Arrays.asList(
                //system has full access
                DomainRoles.ROLE_SYSTEM,
                //director has full access
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_DIRECTOR)
        ));


        //doctors can read all patient data
        hospital.getRolesWithGeneralReadAccess().addAll(Collections.singletonList(
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_DOCTOR)
        ));


        hospital.getExtendedRolesWithGeneralFullAccess().addAll(Arrays.asList(
                //treating doctors have full access
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_DOCTOR))
                        .body(DomainUtil.TREATING_BODY)
                        .build(),
                //admins have full access (log obligation)
                ExtendedDomainRole.builder()
                        .role(DomainRoles.ROLE_ADMIN)
                        .obligation(DomainUtil.LOG_OBLIGATION)
                        .build()
        ));


        hospital.getExtendedRolesWithGeneralReadAccess().addAll(Arrays.asList(
                //treating nurses have read access
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE))
                        .body(DomainUtil.TREATING_BODY)
                        .build(),
                //patients have read access on own data
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_PATIENT))
                        .body(DomainUtil.OWN_DATA_BODY)
                        .build(),
                //relatives have read access on patient data
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR))
                        .body(DomainUtil.RELATIVE_BODY)
                        .build()
        ));
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
