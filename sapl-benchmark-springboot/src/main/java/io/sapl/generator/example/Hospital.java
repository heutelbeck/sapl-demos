package io.sapl.generator.example;

import io.sapl.generator.DomainActions;
import io.sapl.generator.DomainResource;
import io.sapl.generator.DomainRole;
import io.sapl.generator.DomainRole.ExtendedDomainRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Slf4j
@RequiredArgsConstructor
public class Hospital {

    private final String name;

    /* PARAMS */
    private final int numberOfGeneralRoles;
    private final int numberOfGeneralResources;


    private List<DomainRole> hospitalRoles = new ArrayList<>();
    private List<DomainResource> hospitalResources = new ArrayList<>();

    /* ##### GENERAL ##### */
    private List<DomainRole> rolesWithGeneralFullAccess = new ArrayList<>();
    private List<DomainRole> rolesWithGeneralReadAccess = new ArrayList<>();
    private List<DomainRole> rolesWithGeneralSpecialAccess = new ArrayList<>(); // some combination of DomainActions

    private List<ExtendedDomainRole> extendedRolesWithGeneralFullAccess = new ArrayList<>();
    private List<ExtendedDomainRole> extendedRolesWithGeneralReadAccess = new ArrayList<>();

    /* ##### RESOURCE SPECIFIC ##### */

    //e.g. "resource.Patient.Medication => ROLE_DOCTOR => CRUD.ALL
    //e.g. "resource.Patient.Medication => ROLE_PATIENT => CRUD.READ_ONLY
    //e.g. "resource.Patient.Medication => ROLE_VISITOR => CRUD.NONE
    //e.g. "resource.Patient.Room => null (means unrestricted access for all authorized subjects)
    private Map<DomainResource, Map<DomainRole, DomainActions>> resourceSpecificRoleAccess = new HashMap<>();

    private Map<DomainResource, Map<ExtendedDomainRole, DomainActions>> resourceSpecificExtendedRoleAccess = new HashMap<>();


    public void init() {
        generateHospitalRoles();
        generateHospitalResources();
    }

    private void generateHospitalResources() {
        hospitalResources.addAll(ExampleProvider.EXAMPLE_MANDATORY_RESOURCE_LIST.stream()
                .map(DomainResource::new).collect(Collectors.toList()));

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
        hospitalRoles.addAll(ExampleProvider.EXAMPLE_MANDATORY_ROLE_LIST.stream()
                .map(DomainRole::new).collect(Collectors.toList()));

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
