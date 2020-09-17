package io.sapl.demo.generator.example;

import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.DomainRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@RequiredArgsConstructor
public class Hospital {

    private final String name;
    private final int numberOfGeneralRoles;
    private final int numberOfGeneralResources;

    private List<DomainRole> roles = new ArrayList<>();
    private List<DomainResource> resources = new ArrayList<>();


    @PostConstruct
    private void init(){
        for (int i = 0; i < numberOfGeneralRoles; i++) {
            String roleName;
            try {
                roleName = ExampleProvider.EXAMPLE_GENERAL_ROLE_LIST.get(i);
            } catch (IndexOutOfBoundsException ignored) {
                roleName = "ROLE_" + i;
            }
            roles.add(new DomainRole(roleName));
        }


        for (int i = 0; i < numberOfGeneralResources; i++) {
            resources.add(new DomainResource("RESOURCE" + i));
        }
    }
}
