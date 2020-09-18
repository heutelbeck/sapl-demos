package io.sapl.demo.generator.example;

import io.sapl.demo.generator.CRUD;
import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.DomainRole;
import io.sapl.demo.generator.DomainRole.ExtendedDomainRole;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.List;

@Data
@Slf4j
@Builder
public class Department {

    //TODO add resources only available internally (internalResources)

    private String departmentName;
    private int additionalDepartmentRoles;
    private int additionalDepartmentResources;
    private int numberOfSpecialActions;

    private CRUD crudInternal;
    private CRUD crudPublic;

    private List<DomainRole> departmentRoles;
    private List<DomainResource> departmentResources;

    //CRUD actions
    private List<String> departmentInternalActions;
    private List<String> departmentPublicActions;
    //additional actions (specialAction1,...)
    private List<String> departmentSpecialActions;


    private List<DomainRole> rolesForPublicActions;
    private List<DomainRole> rolesForSpecialActions;

    private List<ExtendedDomainRole> extendedRolesForPublicActions;
    private List<ExtendedDomainRole> extendedRolesForSpecialActions;

    @PostConstruct
    private void init() {
        createDepartmentRoles();
        createDepartmentResources();

        createInternalActions();
        createPublicActions();
        createSpecialActions();

        LOGGER.info("initialized department: {}", departmentDetails());
    }

    private void createDepartmentResources() {
        departmentResources.add(new DomainResource("resource." + departmentName));
        for (int i = 0; i < additionalDepartmentResources; i++) {
            departmentResources.add(new DomainResource("resource." + departmentName + i));
        }
    }

    private void createDepartmentRoles() {
        departmentRoles.add(new DomainRole("ROLE_" + departmentName));

        for (int i = 0; i < additionalDepartmentRoles; i++) {
            departmentRoles.add(new DomainRole("ROLE_" + departmentName + "_" + i));
        }
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

    public void addRolesForPublicActions(List<DomainRole> roles) {
        rolesForPublicActions.addAll(roles);
    }

    public void addExtendedRolesForPublicActions(List<ExtendedDomainRole> rolesForAction) {
        extendedRolesForPublicActions.addAll(rolesForAction);
    }

    public void addRolesForSpecialActions(List<DomainRole> roles) {
        rolesForSpecialActions.addAll(roles);
    }

    public void addExtendedRolesForSpecialActions(List<ExtendedDomainRole> rolesForAction) {
        extendedRolesForSpecialActions.addAll(rolesForAction);
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
                ", departmentResources=" + departmentResources +
                '}';
    }


}
