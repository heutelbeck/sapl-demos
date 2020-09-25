package io.sapl.generator;

import io.sapl.generator.DomainPolicy.DomainPolicyAdvice;
import io.sapl.generator.DomainPolicy.DomainPolicyBody;
import io.sapl.generator.DomainPolicy.DomainPolicyObligation;
import io.sapl.generator.DomainPolicy.DomainPolicyTransformation;
import io.sapl.generator.DomainRole.DomainRoles;
import io.sapl.generator.DomainRole.ExtendedDomainRole;
import io.sapl.generator.example.Department;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class DomainGenerator {

    private static final String TAB_STRING = "\t\t";

    private final DomainData domainData;

    private final DomainUtil domainUtil;

    private final String settingsFileContent;


    public void generateDomainPolicies() {
        // HOSPITAL
//        List<DomainPolicy> domainPolicies = generatePoliciesForHospital(domainData.getHospital());
        List<DomainPolicy> domainPolicies = generateHospitalPoliciesNew();
        LOGGER.info("policies DOMAIN: {}", domainPolicies.size());

        List<DomainPolicy> departmentPolicies = new ArrayList<>();
        //DEPARTMENTS
        for (Department department : domainData.getDepartments()) {
            departmentPolicies.addAll(generatePoliciesForDepartment(department));
        }
        LOGGER.info("policies DEPARTMENTS({}): {}", domainData.getNumberOfDepartments(), departmentPolicies.size());
        domainPolicies.addAll(departmentPolicies);

        LOGGER.info("policies TOTAL: {}", domainPolicies.size());


        domainUtil.printDomainPoliciesLimited(domainPolicies);
        domainUtil.writeDomainPoliciesToFilesystem(domainPolicies);
        domainUtil.writeConfigurationInfoToFile(settingsFileContent);

    }

//    private List<DomainPolicy> generatePoliciesForHospital(Hospital hospital) {
//        LOGGER.info("generating policies for hospital");
//        List<DomainPolicy> policiesForHospital = new ArrayList<>();
//
//        for (DomainResource hospitalResource : hospital.getHospitalResources()) {
//            List<DomainPolicy> policiesForResource = new ArrayList<>();
//            /* GENERAL */
//            policiesForResource.addAll(generatePoliciesForGeneral(hospital, hospitalResource));
//
//            /* RESOURCE SPECIFIC */
//            policiesForResource.addAll(generatePoliciesForRoles(hospital, hospitalResource));
//
//            LOGGER.debug("policies HOSPITAL-{}: {}", hospitalResource.getResourceName(), policiesForResource.size());
//            policiesForHospital.addAll(policiesForResource);
//        }
//
//        return policiesForHospital;
//    }

//    private List<DomainPolicy> generatePoliciesForGeneral(Hospital hospital, DomainResource hospitalResource) {
//        List<DomainPolicy> policies = new ArrayList<>();
//        List<DomainResource> resources = Collections.singletonList(hospitalResource);
//
//        //full access
//        policies.add(generateUnrestrictedPolicy(resources, hospital.getRolesWithGeneralFullAccess()));
//
//        // read
//        policies.add(generateActionSpecificPolicy(resources,
//                DomainActions.READ_ONLY.generateActionsForResource(hospitalResource.getResourceName()),
//                hospital.getRolesWithGeneralReadAccess()));
//
//        //extended full access
//        policies.addAll(hospital.getExtendedRolesWithGeneralFullAccess().stream().map(role ->
//                generateActionSpecificExtendedPolicy(resources,
//                        DomainActions.CRUD.generateActionsForResource(hospitalResource.getResourceName()), role))
//                .collect(Collectors.toList())
//        );
//
//        //extended  read access
//        policies.addAll(hospital.getExtendedRolesWithGeneralReadAccess().stream().map(role ->
//                generateActionSpecificExtendedPolicy(resources,
//                        DomainActions.READ_ONLY.generateActionsForResource(hospitalResource.getResourceName()), role))
//                .collect(Collectors.toList())
//        );
//
//        LOGGER.trace("policies HOSPITAL-{}-GENERAL: {}", hospitalResource.getResourceName(), policies.size());
//
//        return policies;
//    }

//    private List<DomainPolicy> generatePoliciesForRoles(Hospital hospital, DomainResource hospitalResource) {
//        List<DomainPolicy> policies = new ArrayList<>();
//        List<DomainResource> resources = Collections.singletonList(hospitalResource);
//
//        Map<DomainRole, DomainActions> roleCRUDMap = hospital.getResourceSpecificRoleAccess().get(hospitalResource);
//        if (!CollectionUtils.isEmpty(roleCRUDMap)) {
//            policies.addAll(roleCRUDMap.entrySet().stream()
//                    .map(entry ->
//                            generateActionSpecificPolicy(resources,
//                                    entry.getValue().generateActionsForResource(hospitalResource.getResourceName()),
//                                    Collections.singletonList(entry.getKey()))
//                    )
//                    .collect(Collectors.toList()));
//        }
//
//        Map<ExtendedDomainRole, DomainActions> extendedDomainRoleCRUDMap =
//                hospital.getResourceSpecificExtendedRoleAccess().get(hospitalResource);
//        if (!CollectionUtils.isEmpty(extendedDomainRoleCRUDMap)) {
//            policies.addAll(extendedDomainRoleCRUDMap.entrySet().stream()
//                    .map(entry ->
//                            generateActionSpecificExtendedPolicy(resources,
//                                    entry.getValue().generateActionsForResource(hospitalResource.getResourceName()),
//                                    entry.getKey())
//                    )
//                    .collect(Collectors.toList()));
//        }
//
//        LOGGER.trace("policies HOSPITAL-{}-ROLES: {}", hospitalResource.getResourceName(), policies.size());
//
//        return policies;
//    }


    private List<DomainPolicy> generatePoliciesForDepartment(Department department) {
        List<DomainPolicy> policiesForDepartment = new ArrayList<>();

        policiesForDepartment.add(generateSystemPolicy(department.getDepartmentResources())); //SYSTEM
        policiesForDepartment.add(generateAdministratorPolicy(department.getDepartmentResources())); //ADMIN
        policiesForDepartment
                .addAll(generateInternalPolicies(department, department.getDepartmentResources()));  //INTERNAL
        policiesForDepartment.addAll(generatePublicPolicies(department, department.getDepartmentResources())); //PUBLIC
        policiesForDepartment
                .addAll(generateSpecialPolicies(department, department.getDepartmentResources())); //SPECIAL

        LOGGER.debug("policies {}: {}", department.getDepartmentName(), policiesForDepartment.size());

        return policiesForDepartment;
    }

    private DomainPolicy generateSystemPolicy(List<DomainResource> resources) {
        return generateUnrestrictedPolicy(resources, Collections.singletonList(DomainRoles.ROLE_SYSTEM));
    }

    private DomainPolicy generateAdministratorPolicy(List<DomainResource> resources) {
        DomainPolicy adminPolicy = generateUnrestrictedPolicy(resources,
                Collections.singletonList(DomainRoles.ROLE_ADMIN));
        //add logging obligation
        StringBuilder policyBuilder = new StringBuilder(adminPolicy.getPolicyContent());
        addObligationToPolicy(policyBuilder, DomainUtil.LOG_OBLIGATION);

        return new DomainPolicy(adminPolicy.getPolicyName(), policyBuilder.toString(), adminPolicy.getFileName());
    }

    private List<DomainPolicy> generateInternalPolicies(Department department, List<DomainResource> resources) {
        List<DomainPolicy> policies = new ArrayList<>();

        if (department.isInternalAccessUnrestricted()) {
            policies.add(generateUnrestrictedPolicy(resources, department.getDepartmentRoles()));
        } else {
            policies.add(generateActionSpecificPolicy(resources, department.getDepartmentInternalActions(),
                    department.getDepartmentRoles()));
        }

        return policies;
    }

    private List<DomainPolicy> generatePublicPolicies(Department department, List<DomainResource> resources) {
        List<DomainPolicy> policies = new ArrayList<>();

        if (department.isPublicAccessUnrestricted()) {
            policies.add(generateUnrestrictedPolicy(resources, department.getRolesForPublicActions()));
        } else {
            policies.add(generateActionSpecificPolicy(resources, department.getDepartmentPublicActions(),
                    department.getRolesForPublicActions()));

            //EXTENDED ROLES(with body/obligation/advice/transformation)
            policies.addAll(department.getExtendedRolesForPublicActions().stream()
                    .map(extendedRole ->
                            generateActionSpecificExtendedPolicy(resources,
                                    department.getDepartmentPublicActions(), extendedRole))
                    .collect(Collectors.toList()));
        }

        return policies;
    }

    private List<DomainPolicy> generateSpecialPolicies(Department department, List<DomainResource> resources) {
        List<DomainPolicy> policies = new ArrayList<>();

        policies.add(generateActionSpecificPolicy(resources, department.getDepartmentSpecialActions(),
                department.getRolesForSpecialActions()));

        //EXTENDED ROLES(with body/obligation/advice/transformation)
        policies.addAll(department.getExtendedRolesForSpecialActions().stream()
                .map(extendedRole ->
                        generateActionSpecificExtendedPolicy(resources, department
                                .getDepartmentSpecialActions(), extendedRole))
                .collect(Collectors.toList()));

        return policies;
    }


    private DomainPolicy generateUnrestrictedPolicy(List<DomainResource> resources, List<DomainRole> roles) {
        String policyName = String.format("%s full access on %s",
                DomainUtil.getRoleNames(roles), DomainUtil.getResourceNames(resources));

        String fileName = String.format("%s_%s", DomainUtil.getResourcesStringForFileName(resources),
                DomainUtil.getRoleNames(roles));

        StringBuilder policyBuilder = generateBasePolicy(policyName, resources);
        addRolesToPolicy(policyBuilder, roles, resources.isEmpty());

        return new DomainPolicy(policyName, policyBuilder.toString(), DomainUtil.sanitizeFileName(fileName));
    }

    private DomainPolicy generateActionSpecificPolicy(List<DomainResource> resources, List<String> actions,
                                                      List<DomainRole> roles) {
        String policyName = String.format("%s can perform %s on %s",
                DomainUtil.getRoleNames(roles), actions, DomainUtil.getResourceNames(resources));

        String fileName = String.format("%s_%s",
                DomainUtil.getResourcesStringForFileName(resources),
                DomainUtil.getRoleNames(roles));


        return new DomainPolicy(policyName, generateBasePolicyWithActions(policyName, resources, actions, roles)
                .toString(), DomainUtil.sanitizeFileName(fileName));
    }


    private DomainPolicy generateActionSpecificExtendedPolicy(List<DomainResource> resources, List<String> actions,
                                                              ExtendedDomainRole extendedRole) {
        String policyName = String.format("[%s] can perform %s on %s (extended: %s)",
                extendedRole.getRole().getRoleName(), actions,
                DomainUtil.getResourceNames(resources),
                DomainUtil.getExtendedRoleIndicator(extendedRole));

        String fileName = String
                .format("%s_%s_extended",
                        DomainUtil.getResourcesStringForFileName(resources),
                        extendedRole.getRole().getRoleName());

        StringBuilder policyBuilder = generateBasePolicyWithActions(policyName, resources, actions,
                DomainRoles.toRole(Collections.singletonList(extendedRole)));

        if (extendedRole.isBodyPresent()) {
            addBodyToPolicy(policyBuilder, extendedRole.getBody());
        }
        if (extendedRole.isObligationPresent()) {
            addObligationToPolicy(policyBuilder, extendedRole.getObligation());
        }
        if (extendedRole.isAdvicePresent()) {
            addAdviceToPolicy(policyBuilder, extendedRole.getAdvice());
        }
        if (extendedRole.isTransformationPresent()) {
            addTransformationToPolicy(policyBuilder, extendedRole.getTransformation());
        }

        return new DomainPolicy(policyName, policyBuilder.toString(), DomainUtil.sanitizeFileName(fileName));
    }

    public StringBuilder generateEmptyPolicy(String policyName) {
        StringBuilder policyBuilder = new StringBuilder().append("policy \"").append(policyName).append("\"")
                .append(System.lineSeparator())
                .append("deny ");

        return policyBuilder;
    }

    public StringBuilder generateGeneralBasePolicy(String policyName, List<DomainRole> roles) {
        StringBuilder policyBuilder = new StringBuilder().append("policy \"").append(policyName).append("\"")
                .append(System.lineSeparator())
                .append("permit ");

        addRolesToPolicy(policyBuilder, roles, true);

        return policyBuilder;
    }

    private StringBuilder generateGeneralBasePolicyWithActions(String policyName, List<String> actions,
                                                               List<DomainRole> roles) {
        StringBuilder policyBuilder = generateGeneralBasePolicy(policyName, roles);
        addActionsToPolicy(policyBuilder, actions);

        return policyBuilder;
    }

    public StringBuilder generateBasePolicy(String policyName, List<DomainResource> resources) {
        StringBuilder policyBuilder = new StringBuilder().append("policy \"").append(policyName).append("\"")
                .append(System.lineSeparator())
                .append("permit ");

        boolean first = true;
        policyBuilder.append("(");
        for (DomainResource resource : resources) {
            if (first) first = false;
            else policyBuilder.append(" | ");
            policyBuilder.append(String.format("resource == \"%s\"", resource.getResourceName()));
        }
        policyBuilder.append(")");

        return policyBuilder;
    }

    private StringBuilder generateBasePolicyWithActions(String policyName, List<DomainResource> resources,
                                                        List<String> actions, List<DomainRole> roles) {
        StringBuilder policyBuilder = generateBasePolicy(policyName, resources);
        addRolesToPolicy(policyBuilder, roles, resources.isEmpty());
        addActionsToPolicy(policyBuilder, actions);

        return policyBuilder;
    }

    public void addRolesToPolicy(StringBuilder policyBuilder, List<DomainRole> roles, boolean emptyPermit) {
        if (roles.isEmpty()) return;

        policyBuilder.append(System.lineSeparator()).append(TAB_STRING);
        if (!emptyPermit) policyBuilder.append(" & ");
        policyBuilder.append("(");

        boolean firstRole = true;
        for (DomainRole role : roles) {
            if (firstRole) firstRole = false;
            else policyBuilder.append(" | ");
            policyBuilder.append(String.format("(\"%s\" in subject..authority)", role.getRoleName()));
        }
        policyBuilder.append(")");
    }

    public void addActionsToPolicy(StringBuilder policyBuilder, List<String> actions) {
        if (actions.isEmpty()) return;

        policyBuilder.append(System.lineSeparator()).append(TAB_STRING).append(" & ").append("(");
        boolean firstAction = true;
        for (String action : actions) {
            if (firstAction) firstAction = false;
            else policyBuilder.append(" | ");
            policyBuilder.append(String.format("action == \"%s\"", action));
        }
        policyBuilder.append(")");
    }

    private void addBodyToPolicy(StringBuilder policyBuilder, DomainPolicyBody body) {
        policyBuilder.append(System.lineSeparator())
                .append("where").append(System.lineSeparator())
                .append(TAB_STRING).append(body.getBody());
    }

    private void addObligationToPolicy(StringBuilder policyBuilder, DomainPolicyObligation obligation) {
        policyBuilder.append(System.lineSeparator())
                .append("obligation").append(System.lineSeparator())
                .append(TAB_STRING).append(obligation.getObligation());
    }

    private void addAdviceToPolicy(StringBuilder policyBuilder, DomainPolicyAdvice advice) {
        policyBuilder.append(System.lineSeparator())
                .append("advice").append(System.lineSeparator())
                .append(TAB_STRING).append(advice.getAdvice());
    }

    private void addTransformationToPolicy(StringBuilder policyBuilder, DomainPolicyTransformation transformation) {
        policyBuilder.append(System.lineSeparator())
                .append("transformation").append(System.lineSeparator())
                .append(TAB_STRING).append(transformation.getTransformation());
    }


    public List<DomainPolicy> generateHospitalPoliciesNew() {

        List<DomainRole> allRoles = generateRoles();
        LOGGER.debug("generated {} roles", allRoles.size());
        List<DomainResource> allResources = generateResources();

        List<DomainResource> unrestrictedResources = allResources.stream().filter(DomainResource::isUnrestricted)
                .collect(Collectors.toList());
        List<DomainResource> restrictedResources = new ArrayList<>(allResources);
        restrictedResources.removeAll(unrestrictedResources);
        LOGGER.debug("generated {} resources (unrestricted={})", allResources.size(), unrestrictedResources.size());

        int newPolicyCount = 0;
        List<DomainPolicy> allPolicies = new ArrayList<>(generateSubjectSpecificPolicies());
        newPolicyCount = allPolicies.size();
        LOGGER.debug("generated {} subject specific policies", newPolicyCount);

        allPolicies.addAll(generateLockedSubjectPolicies());
        newPolicyCount = allPolicies.size() - newPolicyCount;
        LOGGER.debug("generated {} policies for locked subjects", newPolicyCount);

        allPolicies.addAll(generatePoliciesForGeneralAccessRoles(allRoles));
        newPolicyCount = allPolicies.size() - newPolicyCount;
        LOGGER.debug("generated {} policies for general access roles", newPolicyCount);

        allPolicies.addAll(generatePoliciesForUnrestrictedResources(unrestrictedResources));
        newPolicyCount = allPolicies.size() - newPolicyCount;
        LOGGER.debug("generated policies {} for unrestricted resources", newPolicyCount);

        allPolicies.addAll(generatePoliciesForRestrictedResources(restrictedResources, allRoles));
        newPolicyCount = allPolicies.size() - newPolicyCount;
        LOGGER.debug("generated policies {} for restricted resources", newPolicyCount);

//        LOGGER.info("totally generated {} policies for hospital", allPolicies.size());

        return allPolicies;
    }

    private List<DomainPolicy> generatePoliciesForRestrictedResources(List<DomainResource> restrictedResources, List<DomainRole> allRoles) {
        List<DomainPolicy> policies = new ArrayList<>();
        int newPolicyCount = 0;

        List<DomainRole> rolesWithRestrictedAccess = allRoles.stream()
                .filter(role -> !role.isGeneralUnrestrictedAccess())
                .collect(Collectors.toList());

        for (DomainResource resource : restrictedResources) {
            collectAccessingRoles(rolesWithRestrictedAccess, resource);

            if (!resource.getFullAccessRoles().isEmpty())
                handleFullAccessRoles(policies, resource);

            if (!resource.getReadAccessRoles().isEmpty())
                handleReadAccessRoles(policies, resource);

            if (!resource.getCustomAccessRoles().isEmpty())
                handleCustomAccessRoles(policies, resource);

            newPolicyCount = policies.size() - newPolicyCount;
            LOGGER.debug("generated {} policies for resource {}", newPolicyCount, resource.getResourceName());
        }

        return policies;
    }

    private void handleCustomAccessRoles(List<DomainPolicy> policies, DomainResource resource) {
        for (DomainRole customRole : resource.getCustomAccessRoles()) {
            String policyName = resource.getResourceName() + "_custom_" + customRole.getRoleName();

            if (resource.isExtensionRequired()) {
                policyName += "_extended";
            }
            StringBuilder policyBuilder = generateBasePolicyWithActions(policyName, Collections.singletonList(resource),
                    DomainActions.generateCustomActionList(domainData),
                    Collections.singletonList(customRole));

            if (resource.isExtensionRequired()) {
                addObligationToPolicy(policyBuilder, DomainUtil.LOG_OBLIGATION);
            }

            policies.add(new DomainPolicy(policyName, policyBuilder.toString(), policyName));
        }
    }

    private void handleReadAccessRoles(List<DomainPolicy> policies, DomainResource resource) {
        String policyName = resource.getResourceName() + "_read_roles";
        StringBuilder policyBuilder = generateBasePolicyWithActions(policyName, Collections
                .singletonList(resource), DomainActions.READ_ONLY.getActionList(), resource.getReadAccessRoles());

        List<DomainRole> extendedRoles = resource.getReadAccessRoles().stream()
                .filter(DomainRole::isExtensionRequired).collect(Collectors.toList());
        if (resource.isExtensionRequired()) {
            policyName += "_extended";
            addObligationToPolicy(policyBuilder, DomainUtil.LOG_OBLIGATION);

            policies.add(new DomainPolicy(policyName, policyBuilder.toString(), policyName));
        } else if (!extendedRoles.isEmpty()) {
            for (DomainRole extendedRole : extendedRoles) {

                StringBuilder rolePolicyBuilder = new StringBuilder(policyBuilder.toString());
                addObligationToPolicy(rolePolicyBuilder, DomainUtil.LOG_OBLIGATION);

                String rolePolicyName = resource.getResourceName() + "_read_" + extendedRole
                        .getRoleName() + "_extended";

                policies.add(new DomainPolicy(rolePolicyName, rolePolicyBuilder.toString(), rolePolicyName));
            }
        }


    }

    private void handleFullAccessRoles(List<DomainPolicy> policies, DomainResource resource) {
        String policyName = resource.getResourceName() + "_unrestricted-roles";
        StringBuilder policyBuilder = generateBasePolicy(policyName, Collections.singletonList(resource));
        addRolesToPolicy(policyBuilder, resource.getFullAccessRoles(), false);

        List<DomainRole> extendedRoles = resource.getFullAccessRoles().stream()
                .filter(DomainRole::isExtensionRequired).collect(Collectors.toList());
        if (resource.isExtensionRequired()) {
            policyName += "_extended";
            addObligationToPolicy(policyBuilder, DomainUtil.LOG_OBLIGATION);
        } else if (!extendedRoles.isEmpty()) {
            for (DomainRole extendedRole : extendedRoles) {
                StringBuilder rolePolicyBuilder = new StringBuilder(policyBuilder.toString());
                addObligationToPolicy(rolePolicyBuilder, DomainUtil.LOG_OBLIGATION);
                String rolePolicyName = resource.getResourceName() + "_unrestricted_" + extendedRole
                        .getRoleName();

                policies.add(new DomainPolicy(rolePolicyName, rolePolicyBuilder.toString(), rolePolicyName));
            }
        }

        policies.add(new DomainPolicy(policyName, policyBuilder.toString(), policyName));
    }

    private void collectAccessingRoles(List<DomainRole> rolesWithRestrictedAccess, DomainResource resource) {
        for (DomainRole role : rolesWithRestrictedAccess) {
            boolean fullAccessOnResource = domainUtil
                    .rollIsLowerThanProbability(domainData.getProbabilityFullAccessOnResource());
            if (fullAccessOnResource) {
                resource.getFullAccessRoles().add(role);
                continue;
            }
            boolean readAccessOnResource = domainUtil
                    .rollIsLowerThanProbability(domainData.getProbabilityReadAccessOnResource());
            if (readAccessOnResource) {
                resource.getReadAccessRoles().add(role);
                continue;
            }
//            boolean customAccessOnResource = DomainUtil
//                    .rollIsLowerThanProbability(domainData.getProbabilityCustomAccessOnResource());
//            if (customAccessOnResource) {
            resource.getCustomAccessRoles().add(role);
//            }
        }
    }

    private List<DomainPolicy> generatePoliciesForUnrestrictedResources(List<DomainResource> unrestrictedResources) {
        List<DomainPolicy> policies = new ArrayList<>();

        policies.add(new DomainPolicy("unrestricted-resources",
                generateBasePolicy("unrestricted-resources", unrestrictedResources).toString(),
                "unrestricted-resources"
        ));

        return policies;
    }

    private List<DomainPolicy> generatePoliciesForGeneralAccessRoles(List<DomainRole> allRoles) {
        List<DomainPolicy> policies = new ArrayList<>();

        List<DomainRole> unrestrictedRoles = allRoles.stream().filter(DomainRole::isGeneralUnrestrictedAccess)
                .collect(Collectors.toList());
        List<DomainRole> unrestrictedExtensionRoles = unrestrictedRoles.stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());
        unrestrictedRoles.removeAll(unrestrictedExtensionRoles);

        if (!unrestrictedRoles.isEmpty())
            policies.add(new DomainPolicy("general unrestricted roles",
                    generateGeneralBasePolicy("general unrestricted roles", unrestrictedRoles).toString(),
                    "general_unrestricted_roles"
            ));
        //TODO extendedRoles

        List<DomainRole> readRoles = allRoles.stream().filter(DomainRole::isGeneralReadAccess)
                .collect(Collectors.toList());
        List<DomainRole> readExtensionRoles = readRoles.stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());
        readRoles.removeAll(readExtensionRoles);

        if (!readRoles.isEmpty())
            policies.add(new DomainPolicy("general read roles",
                    generateGeneralBasePolicyWithActions("general_read_roles", DomainActions.READ_ONLY
                            .getActionList(), readRoles).toString(), "general_read_roles"
            ));
        //TODO extendedRoles

        List<DomainRole> customRoles = allRoles.stream().filter(DomainRole::isGeneralCustomAccess)
                .collect(Collectors.toList());
        List<DomainRole> customExtensionRoles = customRoles.stream().filter(DomainRole::isExtensionRequired)
                .collect(Collectors.toList());
        customRoles.removeAll(customExtensionRoles);

        if (!customRoles.isEmpty())
            policies.addAll(customRoles.stream()
                    .map(customRole -> new DomainPolicy("general_custom_role_" + customRole.getRoleName(),
                            generateGeneralBasePolicyWithActions("general_custom_role_" + customRole
                                    .getRoleName(), DomainActions
                                    .generateCustomActionList(domainData), readRoles
                            ).toString(), "general_custom_role_" + customRole.getRoleName())
                    ).collect(Collectors.toList()));
        //TODO extendedRoles

        return policies;
    }


    private List<DomainRole> generateRoles() {
        List<DomainRole> roles = new ArrayList<>();

        for (int i = 0; i < domainData.getNumberOfGeneralRoles(); i++) {
            roles.add(new DomainRole(String.format("role.%03d", DomainUtil.getNextRoleCount()),
                    domainUtil.rollIsLowerThanProbability(domainData.getProbabilityOfGeneralFullAccessRole()),
                    domainUtil.rollIsLowerThanProbability(domainData.getProbabilityOfGeneralReadAccessRole()),
                    domainUtil.rollIsLowerThanProbability(domainData.getProbabilityOfGeneralCustomAccessRole()),
                    domainUtil.rollIsLowerThanProbability(domainData.getProbabilityOfExtendedRole())
            ));
        }
        return roles;
    }

    private List<DomainResource> generateResources() {
        List<DomainResource> resources = new ArrayList<>();

        for (int i = 0; i < domainData.getNumberOfGeneralResources(); i++) {
            resources.add(new DomainResource(String.format("resource.%03d", DomainUtil.getNextResourceCount()),
                    domainUtil.rollIsLowerThanProbability(domainData.getProbabilityOfUnrestrictedResource()),
                    domainUtil.rollIsLowerThanProbability(domainData.getProbabilityOfExtendedRole())
            ));
        }
        return resources;
    }

    private List<DomainPolicy> generateSubjectSpecificPolicies() {
        List<DomainPolicy> policies = new ArrayList<>();

        for (int i = 0; i < domainData.getNumberOfSubjects(); i++) {
            String policyName = "policy for subject " + i;

            StringBuilder policyBuilder = generateEmptyPolicy(policyName);

            policies.add(new DomainPolicy(policyName, policyBuilder.toString(), "subject" + i));
        }

        return policies;
    }

    private List<DomainPolicy> generateLockedSubjectPolicies() {
        List<DomainPolicy> policies = new ArrayList<>();

        for (int i = 0; i < domainData.getNumberOfLockedSubjects(); i++) {
            String policyName = "policy for locked subject " + i;

            StringBuilder policyBuilder = generateEmptyPolicy(policyName);
            policies.add(new DomainPolicy(policyName, policyBuilder.toString(), "subject" + i + "_locked"));
        }

        return policies;
    }
}
