package io.sapl.demo.generator.example;

import io.sapl.demo.generator.CRUD;
import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.DomainResource.DomainResources;
import io.sapl.demo.generator.DomainRole;
import io.sapl.demo.generator.DomainRole.DomainRoles;
import io.sapl.demo.generator.example.patientresources.ResourceDiagnosis;
import io.sapl.demo.generator.example.patientresources.ResourceMedication;
import io.sapl.demo.generator.example.patientresources.ResourcePersonalDetails;
import io.sapl.demo.generator.example.patientresources.ResourceRoom;
import io.sapl.demo.generator.example.patientresources.ResourceTreatment;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
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

    //e.g. "resource.Patient.Medication => ROLE_DOCTOR => CRUD.ALL
    //e.g. "resource.Patient.Medication => ROLE_PATIENT => CRUD.READ_ONLY
    //e.g. "resource.Patient.Medication => ROLE_VISITOR => CRUD.NONE
    //e.g. "resource.Patient.Room => null (means unrestricted access for all authorized subjects)
    private Map<DomainResource, Map<DomainRole, CRUD>> resourceRoleActionMap = new HashMap<>();

    @PostConstruct
    private void init() {
        generateHospitalRoles();
        generateHospitalResources();

        rolesWithGeneralFullAccess.addAll(Arrays.asList(
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_DOCTOR),
                DomainRoles.ROLE_ADMIN,
                DomainRoles.ROLE_SYSTEM
        ));
        rolesWithGeneralReadAccess.addAll(Arrays.asList(
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE),
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_PATIENT)
        ));

        addResourcesToMap();
    }

    private void addResourcesToMap() {
        //ROOM
        DomainResource roomResource = DomainResources.findByName(hospitalResources, ResourceRoom.NAME);
        resourceRoleActionMap.put(roomResource, Map.of(
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR), CRUD.READ_ONLY,
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE), CRUD.ALL
        ));

        //PERSONAL DETAILS
        DomainResource personalDetailsResource = DomainResources
                .findByName(hospitalResources, ResourcePersonalDetails.NAME);
        resourceRoleActionMap.put(personalDetailsResource, Map.of(
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR), CRUD.READ_ONLY,
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_PATIENT), CRUD.ALL,
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_NURSE), CRUD.ALL
        ));

        //MEDICATION
        DomainResource medicationResource = DomainResources.findByName(hospitalResources, ResourceMedication.NAME);
        resourceRoleActionMap.put(medicationResource, Map.of());

        //VISITORS CAN READ
        DomainResource treatmentResource = DomainResources.findByName(hospitalResources, ResourceTreatment.NAME);
        resourceRoleActionMap.put(treatmentResource, Map.of(
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR), CRUD.READ_ONLY
        ));

        DomainResource diagnosisResource = DomainResources.findByName(hospitalResources, ResourceDiagnosis.NAME);
        resourceRoleActionMap.put(diagnosisResource, Map.of(
                DomainRoles.findByName(hospitalRoles, ExampleProvider.ROLE_VISITOR), CRUD.READ_ONLY
        ));
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
