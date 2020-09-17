package io.sapl.demo.generator;

import io.sapl.demo.generator.example.Department;
import io.sapl.demo.generator.example.ExampleProvider;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DomainParameter {

    private final int numberOfGeneralRoles;

    private final int numberOfGeneralResources;

    private final int numberOfDepartments;

    private List<DomainRole> generalRoleList;
    private List<DomainResource> generalResourceList;
    private List<Department> departmentList;


    public void init() {
        for (int i = 0; i < numberOfGeneralRoles; i++) {
            String roleName;
            try {
                roleName = ExampleProvider.EXAMPLE_GENERAL_ROLE_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                roleName = "ROLE_" + i;
            }
            generalRoleList.add(new DomainRole(roleName));
        }


        for (int i = 0; i < numberOfGeneralResources; i++) {
            generalResourceList.add(new DomainResource("RESOURCE" + i));
        }

        for (int i = 0; i < numberOfDepartments; i++) {
            String departmentName;
            try {
                departmentName = ExampleProvider.EXAMPLE_DEPARTMENT_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                departmentName = "DEPARTMENT" + i;
            }
            departmentList.add(Department.buildDepartmentWithDefaultValues(departmentName));
        }

    }


}
