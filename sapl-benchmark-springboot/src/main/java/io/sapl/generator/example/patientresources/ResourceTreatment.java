package io.sapl.generator.example.patientresources;


import io.sapl.generator.DomainResource;
import io.sapl.generator.example.ExampleProvider;

public class ResourceTreatment extends DomainResource {

    public static final String NAME = ExampleProvider.RESOURCE_TREATMENT;

    public ResourceTreatment() {
        super(NAME);
    }
}
