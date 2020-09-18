package io.sapl.demo.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class DomainResource {

    private final String resourceName;


    public static class DomainResources {
        public static DomainResource findByName(List<DomainResource> resourceList, String resourceName) {
            return resourceList.stream()
                    .filter(domainResource -> domainResource.getResourceName().equalsIgnoreCase(resourceName))
                    .findFirst().orElseThrow();
        }
    }


}
