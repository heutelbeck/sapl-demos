package io.sapl.demo.generator;

import io.sapl.demo.generator.example.Department;
import io.sapl.demo.generator.example.ExampleProvider;
import io.sapl.demo.generator.example.Hospital;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.List;

@Data
@RequiredArgsConstructor
public class DomainData {

    private final int numberOfGeneralRoles;
    private final int numberOfGeneralResources;
    private final int numberOfDepartments;

    private Hospital hospital;
    private List<Department> departments;


    @PostConstruct
    public void init() {
        hospital = new Hospital("Demo Hospital GmbH", numberOfGeneralRoles, numberOfGeneralResources);

        for (int i = 0; i < numberOfDepartments; i++) {
            String departmentName;
            try {
                departmentName = ExampleProvider.EXAMPLE_DEPARTMENT_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                departmentName = "DEPARTMENT" + i;
            }
            departments.add(Department.buildDepartmentWithDefaultValues(departmentName));
        }

    }


}
