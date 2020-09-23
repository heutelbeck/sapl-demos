package io.sapl.generator.example.patientresources;


import io.sapl.generator.DomainResource;
import io.sapl.generator.example.ExampleProvider;

public class ResourceRoom extends DomainResource {

    public static final String NAME = ExampleProvider.RESOURCE_ROOM;

    public ResourceRoom() {
        super(NAME);
    }
}
