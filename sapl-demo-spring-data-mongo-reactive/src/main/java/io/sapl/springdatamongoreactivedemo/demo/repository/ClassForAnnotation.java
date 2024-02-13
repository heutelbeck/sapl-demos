package io.sapl.springdatamongoreactivedemo.demo.repository;

import lombok.experimental.UtilityClass;


@UtilityClass
public class ClassForAnnotation {

    public boolean setResource(int age) {

        return age >= 18;
    }

    public String setEnvironment(Role role) {

        return role.name();
    }
}
