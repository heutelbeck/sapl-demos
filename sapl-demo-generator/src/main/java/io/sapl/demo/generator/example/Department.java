package io.sapl.demo.generator.example;

import io.sapl.demo.generator.CRUD;
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

    private List<String> departmentRoles = new ArrayList<>();
    private List<String> departmentResources = new ArrayList<>();

    private List<String> departmentInternalActions = new ArrayList<>();
    private List<String> departmentPublicActions = new ArrayList<>();
    private List<String> departmentSpecialActions = new ArrayList<>();

    private List<String> rolesForPublicActions = new ArrayList<>();
    private List<String> rolesForSpecialActions = new ArrayList<>();


    public static Department buildDepartmentWithDefaultValues(String departmentName) {
        return new Department(departmentName, 0,
                0,
                0, CRUD.ALL, CRUD.READ_ONLY);
    }

    @PostConstruct
    private void init() {
        departmentRoles.add("ROLE_" + departmentName);

        for (int i = 0; i < numberOfAdditionalDepartmentSpecificRoles; i++) {
            departmentRoles.add("ROLE_" + departmentName + "_" + i);
        }

        departmentResources.add("resource." + departmentName);
        for (int i = 0; i < numberOfAdditionalDepartmentSpecificResources; i++) {
            departmentResources.add("resource." + departmentName + i);
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
        for (String departmentResource : departmentResources) {
            departmentPublicActions.addAll(crudPublic.generateActionsWithName(departmentResource));
        }
    }

    private void createInternalActions() {
        for (String departmentResource : departmentResources) {
            departmentInternalActions.addAll(crudInternal.generateActionsWithName(departmentResource));
        }
    }

    public void addRolesForPublicActions(String... roles) {
        rolesForPublicActions.addAll(Arrays.asList(roles.clone()));
    }

    public void addRolesForSpecialActions(String... roles) {
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
