package io.sapl.generator.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleProvider {

    /* ### DEPARTMENTS ### */
    public static final String DEPARTMENT_ANESTHESIOLOGY = "anesthesiology";
    public static final String DEPARTMENT_BOARD = "board";
    public static final String DEPARTMENT_CARDIOLOGY = "cardiology";
    public static final String DEPARTMENT_FINANCE = "finance";
    public static final String DEPARTMENT_GYNECOLOGY = "gynecology";
    public static final String DEPARTMENT_HUMAN_RESOURCES = "human_resources";
    public static final String DEPARTMENT_LABORATORY = "laboratory";
    public static final String DEPARTMENT_MANAGEMENT = "management";
    public static final String DEPARTMENT_NEUROLOGY = "neurology";
    public static final String DEPARTMENT_ONCOLOGY = "oncology";
    public static final String DEPARTMENT_PHARMACY = "pharmacy";
    public static final String DEPARTMENT_PURCHASING_SUPPLIES = "purchasing_supplies";
    public static final String DEPARTMENT_RADIOLOGY = "radiology";
    public static final String DEPARTMENT_SURGERY = "surgery";

    public static final List<String> EXAMPLE_DEPARTMENT_LIST = new ArrayList<>();

    static {
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_ANESTHESIOLOGY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_BOARD);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_CARDIOLOGY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_FINANCE);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_GYNECOLOGY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_HUMAN_RESOURCES);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_LABORATORY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_MANAGEMENT);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_NEUROLOGY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_ONCOLOGY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_PHARMACY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_PURCHASING_SUPPLIES);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_RADIOLOGY);
        EXAMPLE_DEPARTMENT_LIST.add(DEPARTMENT_SURGERY);
    }

    /* ### PER DEPARTMENT ROLES ### */
    public static final String ROLE_ANESTHETIST = "anesthetist";
    public static final String ROLE_CARDIOLOGIST = "cardiologist";
    public static final String ROLE_CENTRAL_BUYER = "central_buyer";
    public static final String ROLE_CLINICAL_MANAGER = "manager";
    public static final String ROLE_DIRECTOR = "director";
    public static final String ROLE_FINANCE = "finance";
    public static final String ROLE_GYNECOLOGIST = "gynecologist";
    public static final String ROLE_HUMAN_RESOURCES = "human_resources";
    public static final String ROLE_LAB_TECHNICIAN = "lab_technician";
    public static final String ROLE_NEUROLOGIST = "neurologist";
    public static final String ROLE_ONCOLOGIST = "oncologist";
    public static final String ROLE_PHARMACIST = "pharmacist";
    public static final String ROLE_RADIOLOGIST = "radiologist";
    public static final String ROLE_SURGEON = "surgeon";

    public static final String ROLE_DOCTOR = "doctor";
    public static final String ROLE_NURSE = "nurse";
    public static final String ROLE_PATIENT = "patient";
    public static final String ROLE_VISITOR = "visitor";

    //role appendix
    public static final String ROLE_APPENDIX_ASSISTANCE = "assistance";
    public static final String ROLE_APPENDIX_INTERN = "intern";
    public static final String ROLE_APPENDIX_STUDENT = "student";
    public static final String ROLE_APPENDIX_VOLUNTEER = "volunteer";


    public static final List<String> EXAMPLE_MANDATORY_ROLE_LIST = new ArrayList<>();
    public static final List<String> EXAMPLE_GENERAL_ROLE_LIST = new ArrayList<>();
    public static final List<String> EXAMPLE_GENERAL_ROLE_APPENDIX_LIST = new ArrayList<>();


    public static final Map<String, String> DEPARTMENT_ROLE_MAP = new HashMap<>();

    static {
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_ANESTHETIST);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_CARDIOLOGIST);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_CENTRAL_BUYER);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_CLINICAL_MANAGER);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_FINANCE);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_GYNECOLOGIST);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_HUMAN_RESOURCES);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_LAB_TECHNICIAN);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_NEUROLOGIST);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_ONCOLOGIST);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_PHARMACIST);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_RADIOLOGIST);
        EXAMPLE_GENERAL_ROLE_LIST.add(ROLE_SURGEON);

        EXAMPLE_MANDATORY_ROLE_LIST.add(ROLE_DIRECTOR);
        EXAMPLE_MANDATORY_ROLE_LIST.add(ROLE_DOCTOR);
        EXAMPLE_MANDATORY_ROLE_LIST.add(ROLE_NURSE);
        EXAMPLE_MANDATORY_ROLE_LIST.add(ROLE_PATIENT);
        EXAMPLE_MANDATORY_ROLE_LIST.add(ROLE_VISITOR);

        EXAMPLE_GENERAL_ROLE_APPENDIX_LIST.add(ROLE_APPENDIX_ASSISTANCE);
        EXAMPLE_GENERAL_ROLE_APPENDIX_LIST.add(ROLE_APPENDIX_STUDENT);
        EXAMPLE_GENERAL_ROLE_APPENDIX_LIST.add(ROLE_APPENDIX_INTERN);
        EXAMPLE_GENERAL_ROLE_APPENDIX_LIST.add(ROLE_APPENDIX_VOLUNTEER);

        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_ANESTHESIOLOGY, ROLE_ANESTHETIST);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_CARDIOLOGY, ROLE_CARDIOLOGIST);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_BOARD, ROLE_DIRECTOR);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_FINANCE, ROLE_FINANCE);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_GYNECOLOGY, ROLE_GYNECOLOGIST);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_HUMAN_RESOURCES, ROLE_HUMAN_RESOURCES);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_LABORATORY, ROLE_LAB_TECHNICIAN);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_MANAGEMENT, ROLE_CLINICAL_MANAGER);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_NEUROLOGY, ROLE_NEUROLOGIST);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_ONCOLOGY, ROLE_ONCOLOGIST);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_PHARMACY, ROLE_PHARMACIST);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_PURCHASING_SUPPLIES, ROLE_CENTRAL_BUYER);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_RADIOLOGY, ROLE_RADIOLOGIST);
        DEPARTMENT_ROLE_MAP.put(DEPARTMENT_SURGERY, ROLE_SURGEON);
    }


    /* ### RESOURCES ### */
    public static final String RESOURCE_DIAGNOSIS = "diagnosis";
    public static final String RESOURCE_MEDICATION = "medication";
    public static final String RESOURCE_PERSONAL_DETAILS = "personal_details";
    public static final String RESOURCE_ROOM = "room";
    public static final String RESOURCE_TREATMENT = "treatment";
    public static final String RESOURCE_INSURANCE = "insurance";
    public static final String RESOURCE_INVOICES = "invoices";
    public static final String RESOURCE_STAFF = "staff";
    public static final String RESOURCE_DEPARTMENTS = "departments";
    public static final String RESOURCE_MEDICAL_DEVICES = "medical_devices";
    public static final String RESOURCE_MEDICAL_SUPPLIES = "medical_supplies";

    public static final List<String> EXAMPLE_MANDATORY_RESOURCE_LIST = new ArrayList<>();
    public static final List<String> EXAMPLE_GENERAL_RESOURCE_LIST = new ArrayList<>();


    static {
        EXAMPLE_MANDATORY_RESOURCE_LIST.add(RESOURCE_DIAGNOSIS);
        EXAMPLE_MANDATORY_RESOURCE_LIST.add(RESOURCE_MEDICATION);
        EXAMPLE_MANDATORY_RESOURCE_LIST.add(RESOURCE_TREATMENT);
        EXAMPLE_MANDATORY_RESOURCE_LIST.add(RESOURCE_PERSONAL_DETAILS);
        EXAMPLE_MANDATORY_RESOURCE_LIST.add(RESOURCE_ROOM);

        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_INSURANCE);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_INVOICES);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_DEPARTMENTS);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_MEDICAL_DEVICES);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_MEDICAL_SUPPLIES);
        EXAMPLE_GENERAL_RESOURCE_LIST.add(RESOURCE_STAFF);
    }


}
