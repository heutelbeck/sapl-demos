package io.sapl.demo.generator.example.patientresources;

import io.sapl.demo.generator.DomainResource;
import io.sapl.demo.generator.example.ExampleProvider;

public class ResourceRoom extends DomainResource {

    public static final String NAME = ExampleProvider.RESOURCE_ROOM;

    public ResourceRoom() {
        super(NAME);
    }
}
