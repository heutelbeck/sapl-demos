package io.sapl.demo.generator.example;

import java.util.ArrayList;
import java.util.List;

public class ExampleProvider {

    /* ### DEPARTMENTS ### */
    public static final String DEPARTMENT_ANESTHESIOLOGY = "ANESTHESIOLOGY";
    public static final String DEPARTMENT_SURGERY = "SURGERY";
    public static final String DEPARTMENT_LABORATORY = "LABORATORY";
    public static final String DEPARTMENT_RADIOLOGY = "RADIOLOGY";

    public static final List<String> EXAMPLE_DEPARTMENT_LIST = new ArrayList<>();

    /* ### ROLES ### */
    public static final String ROLE_DIRECTOR = "ROLE_DIRECTOR";
    public static final String ROLE_DOCTOR = "ROLE_DOCTOR";
    public static final String ROLE_NURSE = "ROLE_NURSE";

    public static final String ROLE_PATIENT = "ROLE_PATIENT";
    public static final String ROLE_VISITOR = "ROLE_VISITOR";

    public static final List<String> EXAMPLE_GENERAL_ROLE_LIST = new ArrayList<>();


    /* ### RESOURCES ### */
    public static final String RESOURCE_DIAGNOSIS = "RESOURCE_DIAGNOSIS";
    public static final String RESOURCE_MEDICATION = "RESOURCE_MEDICATION";
    public static final String RESOURCE_PERSONAL_DETAILS = "RESOURCE_PERSONAL_DETAILS";
    public static final String RESOURCE_ROOM = "RESOURCE_ROOM";
    public static final String RESOURCE_TREATMENT = "RESOURCE_TREATMENT";

    public static final List<String> EXAMPLE_GENERAL_RESOURCE_LIST = new ArrayList<>();


    static {
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_ANESTHESIOLOGY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_SURGERY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_LABORATORY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_RADIOLOGY);

        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_DIRECTOR);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_DOCTOR);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_NURSE);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_PATIENT);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_VISITOR);

        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_DIAGNOSIS);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_MEDICATION);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_PERSONAL_DETAILS);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_ROOM);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_TREATMENT);

    }





}
