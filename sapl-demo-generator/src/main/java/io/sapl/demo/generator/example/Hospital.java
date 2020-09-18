package io.sapl.demo.generator.example;

import io.sapl.demo.generator.DomainActions;
import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.DomainRole;
import io.sapl.demo.generator.DomainRole.ExtendedDomainRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@RequiredArgsConstructor
public class Hospital {



    private final String name;
    private final int numberOfGeneralRoles;
    private final int numberOfGeneralResources;

    private List<DomainRole> hospitalRoles = new ArrayList<>();
    private List<DomainResource> hospitalResources = new ArrayList<>();

    private List<DomainRole> rolesWithGeneralFullAccess = new ArrayList<>();
    private List<DomainRole> rolesWithGeneralReadAccess = new ArrayList<>();

    private List<ExtendedDomainRole> extendedRolesWithGeneralFullAccess = new ArrayList<>();
    private List<ExtendedDomainRole> extendedRolesWithGeneralReadAccess = new ArrayList<>();

    //e.g. "resource.Patient.Medication => ROLE_DOCTOR => CRUD.ALL
    //e.g. "resource.Patient.Medication => ROLE_PATIENT => CRUD.READ_ONLY
    //e.g. "resource.Patient.Medication => ROLE_VISITOR => CRUD.NONE
    //e.g. "resource.Patient.Room => null (means unrestricted access for all authorized subjects)
    private Map<DomainResource, Map<DomainRole, DomainActions>> resourceSpecificRoleAccess = new HashMap<>();

    private Map<DomainResource, Map<ExtendedDomainRole, DomainActions>> resourceSpecificExtendedRoleAccess = new HashMap<>();

    @PostConstruct
    private void init() {
        generateHospitalRoles();
        generateHospitalResources();

    }

    private void generateHospitalResources() {
        for (int i = 0; i < numberOfGeneralResources; i++) {
            String resourceName;
            try {
                resourceName = ExampleProvider.EXAMPLE_GENERAL_RESOURCE_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                resourceName = "resource." + i;
            }
            hospitalResources.add(new DomainResource(resourceName));
        }
    }

    private void generateHospitalRoles() {

        for (int i = 0; i < numberOfGeneralRoles; i++) {
            String roleName;
            try {
                roleName = ExampleProvider.EXAMPLE_GENERAL_ROLE_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                roleName = "ROLE_" + i;
            }
            hospitalRoles.add(new DomainRole(roleName));
        }
    }


}
