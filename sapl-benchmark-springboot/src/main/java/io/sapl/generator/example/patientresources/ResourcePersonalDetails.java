package io.sapl.generator.example.patientresources;


import io.sapl.generator.DomainResource;
import io.sapl.generator.example.ExampleProvider;

public class ResourcePersonalDetails extends DomainResource {

    public static final String NAME = ExampleProvider.RESOURCE_PERSONAL_DETAILS;

    public ResourcePersonalDetails() {
        super(NAME);
    }

}
