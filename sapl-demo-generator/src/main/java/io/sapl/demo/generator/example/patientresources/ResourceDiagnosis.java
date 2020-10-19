package io.sapl.demo.generator.example.patientresources;

import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.example.ExampleProvider;

public class ResourceDiagnosis extends DomainResource {

    public static final String NAME = ExampleProvider.RESOURCE_DIAGNOSIS;

    public ResourceDiagnosis() {
        super(NAME);
    }
}
