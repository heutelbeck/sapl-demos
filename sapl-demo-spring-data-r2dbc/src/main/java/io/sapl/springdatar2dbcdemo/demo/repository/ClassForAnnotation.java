package io.sapl.springdatar2dbcdemo.demo.repository;

import lombok.experimental.UtilityClass;


@UtilityClass
public class ClassForAnnotation {

    public boolean setResource(int age) {

        return age >= 18;
    }

    public boolean setEnvironment(boolean active) {

        return active;
    }
}
