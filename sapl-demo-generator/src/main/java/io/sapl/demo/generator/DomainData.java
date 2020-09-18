package io.sapl.demo.generator;

import io.sapl.demo.generator.DomainRole.DomainRoles;
import io.sapl.demo.generator.DomainRole.ExtendedDomainRole;
import io.sapl.demo.generator.example.Department;
import io.sapl.demo.generator.example.ExampleProvider;
import io.sapl.demo.generator.example.Hospital;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
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

    @PostConstruct
    public void init() {
        //TODO create policies for hospital/general resources & roles
        hospital = new Hospital("Demo Hospital GmbH", numberOfGeneralRoles, numberOfGeneralResources);

        boolean additionalRoles = dice.nextDouble() > probabilityOfAdditionalRoles;
        boolean additionalResources = dice.nextDouble() > probabilityOfAdditionalResources;

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
                    .crudInternal(CRUD.ALL)
                    .crudPublic(CRUD.READ_ONLY)
                    //TODO specials
//                    .additionalDepartmentRoles(additionalRoles ? dice.nextInt(3) + 1 : 0)
//                    .additionalDepartmentResources(additionalResources ? dice.nextInt(3) + 1 : 0)
                    .build();

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

            departments.add(department);
        }

    }

}
