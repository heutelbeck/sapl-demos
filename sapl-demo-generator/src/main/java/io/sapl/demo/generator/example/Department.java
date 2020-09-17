package io.sapl.demo.generator.example;

import io.sapl.demo.generator.CRUD;
import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.DomainRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Slf4j
@RequiredArgsConstructor
public class Department {

    private final String departmentName;
    private final int numberOfAdditionalDepartmentSpecificRoles;
    private final int numberOfAdditionalDepartmentSpecificResources;
    private final int numberOfSpecialActions;

    private final CRUD crudInternal;
    private final CRUD crudPublic;

    private List<DomainRole> departmentRoles = new ArrayList<>();
    private List<DomainResource> departmentResources = new ArrayList<>();

    private List<String> departmentInternalActions = new ArrayList<>();
    private List<String> departmentPublicActions = new ArrayList<>();
    private List<String> departmentSpecialActions = new ArrayList<>();

    private List<DomainRole> rolesForPublicActions = new ArrayList<>();
    private List<DomainRole> rolesForSpecialActions = new ArrayList<>();


    public static Department buildDepartmentWithDefaultValues(String departmentName) {
        return new Department(departmentName, 0,
                0,
                0, CRUD.ALL, CRUD.READ_ONLY);
    }

    @PostConstruct
    private void init() {
        departmentRoles.add(new DomainRole("ROLE_" + departmentName));

        for (int i = 0; i < numberOfAdditionalDepartmentSpecificRoles; i++) {
            departmentRoles.add(new DomainRole("ROLE_" + departmentName + "_" + i));
        }

        departmentResources.add(new DomainResource("resource." + departmentName));
        for (int i = 0; i < numberOfAdditionalDepartmentSpecificResources; i++) {
            departmentResources.add(new DomainResource("resource." + departmentName + i));
        }

        createInternalActions();
        createPublicActions();
        createSpecialActions();

        LOGGER.info("initialized department: {}", departmentDetails());
    }


    private void createSpecialActions() {
        for (int i = 0; i < numberOfSpecialActions; i++) {
            departmentSpecialActions.add("specialAction" + i);
        }
    }

    private void createPublicActions() {
        for (DomainResource departmentResource : departmentResources) {
            departmentPublicActions.addAll(crudPublic.generateActionsWithName(departmentResource.getResourceName()));
        }
    }

    private void createInternalActions() {
        for (DomainResource departmentResource : departmentResources) {
            departmentInternalActions
                    .addAll(crudInternal.generateActionsWithName(departmentResource.getResourceName()));
        }
    }

    public void addRolesForPublicActions(DomainRole... roles) {
        rolesForPublicActions.addAll(Arrays.asList(roles.clone()));
    }

    public void addRolesForSpecialActions(DomainRole... roles) {
        rolesForSpecialActions.addAll(Arrays.asList(roles.clone()));
    }

    public boolean isInternalAccessUnrestricted() {
        return crudInternal == CRUD.ALL;
    }

    public boolean isPublicAccessUnrestricted() {
        return crudPublic == CRUD.ALL;
    }

    public String departmentDetails() {
        return "Department{" +
                "departmentName='" + departmentName + '\'' +
                ", departmentRoles=" + departmentRoles +
                ", rolesForPublicActions=" + rolesForPublicActions +
                ", rolesForSpecialActions=" + rolesForSpecialActions +
                ", isInternalAccessUnrestricted=" + isInternalAccessUnrestricted() +
                ", isPublicAccessUnrestricted=" + isPublicAccessUnrestricted() +
                '}';
    }
}
