package io.sapl.demo.generator;

import io.sapl.demo.generator.DomainResource.DomainResources;
import io.sapl.demo.generator.DomainRole.DomainRoles;
import io.sapl.demo.generator.DomainRole.ExtendedDomainRole;
import io.sapl.demo.generator.example.Department;
import io.sapl.demo.generator.example.ExampleProvider;
import io.sapl.demo.generator.example.Hospital;
import io.sapl.demo.generator.example.patientresources.ResourceDiagnosis;
import io.sapl.demo.generator.example.patientresources.ResourceMedication;
import io.sapl.demo.generator.example.patientresources.ResourcePersonalDetails;
import io.sapl.demo.generator.example.patientresources.ResourceRoom;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Slf4j
@RequiredArgsConstructor
public class DomainData {

    //TODO: in the final implementation this should be abstract instead of using Hospital and Department

    private final int numberOfGeneralRoles;
    private final int numberOfGeneralResources;
    private final int numberOfDepartments;


    private final double probabilityOfAdditionalRoles;
    private final double probabilityOfAdditionalResources;

    private Hospital hospital;
    private List<Department> departments;

    private Random dice = new Random();
    private List<DomainRole> hospitalRoles;
    private List<DomainResource> hospitalResources;

    @PostConstruct
    public void init() {
        createHospital();

        createDepartments();

    }

    private void createHospital() {
        this.hospital = new Hospital("Demo Hospital GmbH", numberOfGeneralRoles, numberOfGeneralResources);
        this.hospitalRoles = hospital.getHospitalRoles();
        this.hospitalResources = hospital.getHospitalResources();


        createHospitalGeneralAccess();
        createHospitalResourceSpecificRoleAccess();
        createHospitalResourceSpecificExtendedRoleAccess();

    }

    private void createHospitalResourceSpecificExtendedRoleAccess() {
        Map<DomainResource, Map<ExtendedDomainRole, DomainActions>> map = hospital
                .getResourceSpecificExtendedRoleAccess();

        map.put(DomainResources.findByName(hospitalResources, ResourceRoom.NAME), Map.of(
                //Patients have full access on own personal details
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_PATIENT))
                        .body(DomainUtil.OWN_DATA_BODY)
                        .build(), DomainActions.ALL,
                //Relatives have read access on personal details
                ExtendedDomainRole.builder()
                        .role(DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR))
                        .body(DomainUtil.RELATIVE_BODY)
                        .build(), DomainActions.READ_ONLY
        ));

        map.put(DomainResources.findByName(hospitalResources, ResourceDiagnosis.NAME), Map.of(
                //Relatives have read access on diagnosis
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
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE), DomainActions.ALL
        ));

        //PERSONAL DETAILS
        DomainResource personalDetailsResource = DomainResources
                .findByName(hospitalResources, ResourcePersonalDetails.NAME);
        resourceSpecificRoleAccess.put(personalDetailsResource, Map.of(
                // Nurse has full access on personal details in addition to general read permission
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE), DomainActions.ALL
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
                        .obligation(DomainUtil.LOGGING_OBLIGATION)
                        .build()
        ));

        hospital.getExtendedRolesWithGeneralFullAccess().addAll(Arrays.asList(
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
//        boolean additionalRoles = dice.nextDouble() > probabilityOfAdditionalRoles;
//        boolean additionalResources = dice.nextDouble() > probabilityOfAdditionalResources;

        //TODO department creation
        // - create different departments
        for (int i = 0; i < numberOfDepartments; i++) {
            String departmentName;
            try {
                departmentName = ExampleProvider.EXAMPLE_DEPARTMENT_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                departmentName = "DEPARTMENT" + i;
            }
            Department department = Department.builder()
                    .departmentName(departmentName)
                    .numberOfSpecialActions(5)
                    .domainActionsInternal(DomainActions.ALL)
                    .domainActionsPublic(DomainActions.READ_ONLY)
                    //TODO specials
//                    .additionalDepartmentRoles(additionalRoles ? dice.nextInt(3) + 1 : 0)
//                    .additionalDepartmentResources(additionalResources ? dice.nextInt(3) + 1 : 0)
                    .build();

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
                        .obligation(DomainUtil.LOGGING_OBLIGATION)
                        .build()
        ));
    }

}
