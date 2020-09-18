package io.sapl.demo.generator.example.patientresources;

import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.example.ExampleProvider;

public class ResourcePersonalDetails extends DomainResource {

    public static final String NAME = ExampleProvider.RESOURCE_PERSONAL_DETAILS;

    public ResourcePersonalDetails() {
        super(NAME);
    }

}
