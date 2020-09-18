package io.sapl.demo.generator;

import io.sapl.demo.generator.DomainPolicy.DomainPolicyAdvice;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyBody;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyObligation;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyTransformation;
import io.sapl.demo.generator.DomainRole.DomainRoles;
import io.sapl.demo.generator.DomainRole.ExtendedDomainRole;
import io.sapl.demo.generator.example.Department;
import io.sapl.demo.generator.example.Hospital;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainGenerator {

    private final static String TAB_STRING = "\t\t";

    private final DomainData domainData;

    private final DomainUtil domainUtil;


    public void generateDomainPolicies() {
        // HOSPITAL
        List<DomainPolicy> domainPolicies = generatePoliciesForHospital(domainData.getHospital());
        LOGGER.info("policies HOSPITAL: {}", domainPolicies.size());


        List<DomainPolicy> departmentPolicies = new ArrayList<>();
        //DEPARTMENTS
        for (Department department : domainData.getDepartments()) {
            departmentPolicies.addAll(generatePoliciesForDepartment(department));
        }
        LOGGER.info("policies DEPARTMENTS: {}", departmentPolicies.size());
        domainPolicies.addAll(departmentPolicies);

        LOGGER.info("policies TOTAL: {}", domainPolicies.size());

        domainUtil.printDomainPoliciesLimited(domainPolicies);
        domainUtil.writeDomainPoliciesToFilesystem(domainPolicies);
    }

    private List<DomainPolicy> generatePoliciesForHospital(Hospital hospital) {
        LOGGER.info("generating policies for hospital");
        List<DomainPolicy> policiesForHospital = new ArrayList<>();

        for (DomainResource hospitalResource : hospital.getHospitalResources()) {
            List<DomainPolicy> policiesForResource = new ArrayList<>();
            /* GENERAL */
            policiesForResource.addAll(generatePoliciesForGeneral(hospital, hospitalResource));

            /* RESOURCE SPECIFIC */
            policiesForResource.addAll(generatePoliciesForRoles(hospital, hospitalResource));

            LOGGER.debug("policies HOSPITAL-{}: {}", hospitalResource.getResourceName(), policiesForResource.size());
            policiesForHospital.addAll(policiesForResource);
        }

        return policiesForHospital;
    }

    private List<DomainPolicy> generatePoliciesForGeneral(Hospital hospital, DomainResource hospitalResource) {
        List<DomainPolicy> policies = new ArrayList<>();
        //full access
        policies.add(generateUnrestrictedPolicy(hospitalResource, hospital.getRolesWithGeneralFullAccess()));

        // read
        policies.add(generateActionSpecificPolicy(hospitalResource,
                DomainActions.READ_ONLY.generateActionsForResource(hospitalResource.getResourceName()),
                hospital.getRolesWithGeneralReadAccess()));

        //extended full access
        policies.addAll(hospital.getExtendedRolesWithGeneralFullAccess().stream().map(role ->
                generateActionSpecificExtendedPolicy(hospitalResource,
                        DomainActions.CRUD.generateActionsForResource(hospitalResource.getResourceName()), role))
                .collect(Collectors.toList())
        );

        //extended  read access
        policies.addAll(hospital.getExtendedRolesWithGeneralReadAccess().stream().map(role ->
                generateActionSpecificExtendedPolicy(hospitalResource,
                        DomainActions.READ_ONLY.generateActionsForResource(hospitalResource.getResourceName()), role))
                .collect(Collectors.toList())
        );

        LOGGER.trace("policies HOSPITAL-{}-GENERAL: {}", hospitalResource.getResourceName(), policies.size());

        return policies;
    }

    private List<DomainPolicy> generatePoliciesForRoles(Hospital hospital, DomainResource hospitalResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        Map<DomainRole, DomainActions> roleCRUDMap = hospital.getResourceSpecificRoleAccess().get(hospitalResource);
        if (!CollectionUtils.isEmpty(roleCRUDMap)) {
            policies.addAll(roleCRUDMap.entrySet().stream()
                    .map(entry ->
                            generateActionSpecificPolicy(hospitalResource,
                                    entry.getValue().generateActionsForResource(hospitalResource.getResourceName()),
                                    Collections.singletonList(entry.getKey()))
                    )
                    .collect(Collectors.toList()));
        }

        Map<ExtendedDomainRole, DomainActions> extendedDomainRoleCRUDMap =
                hospital.getResourceSpecificExtendedRoleAccess().get(hospitalResource);
        if (!CollectionUtils.isEmpty(extendedDomainRoleCRUDMap)) {
            policies.addAll(extendedDomainRoleCRUDMap.entrySet().stream()
                    .map(entry ->
                            generateActionSpecificExtendedPolicy(hospitalResource,
                                    entry.getValue().generateActionsForResource(hospitalResource.getResourceName()),
                                    entry.getKey())
                    )
                    .collect(Collectors.toList()));
        }

        LOGGER.trace("policies HOSPITAL-{}-ROLES: {}", hospitalResource.getResourceName(), policies.size());

        return policies;
    }


    private List<DomainPolicy> generatePoliciesForDepartment(Department department) {
        List<DomainPolicy> policiesForDepartment = new ArrayList<>();

        for (DomainResource departmentResource : department.getDepartmentResources()) {
            policiesForDepartment.add(generateSystemPolicy(departmentResource)); //SYSTEM
            policiesForDepartment.add(generateAdministratorPolicy(departmentResource)); //ADMIN

            policiesForDepartment.addAll(generateInternalPolicies(department, departmentResource));  //INTERNAL
            policiesForDepartment.addAll(generatePublicPolicies(department, departmentResource)); //PUBLIC
            policiesForDepartment.addAll(generateSpecialPolicies(department, departmentResource)); //SPECIAL
        }

        LOGGER.debug("policies {}: {}", department.getDepartmentName(), policiesForDepartment.size());

        return policiesForDepartment;
    }

    private DomainPolicy generateSystemPolicy(DomainResource resource) {
        return generateUnrestrictedPolicy(resource, Collections.singletonList(DomainRoles.ROLE_SYSTEM));
    }

    private DomainPolicy generateAdministratorPolicy(DomainResource resource) {
        DomainPolicy adminPolicy = generateUnrestrictedPolicy(resource,
                Collections.singletonList(DomainRoles.ROLE_ADMIN));
        //add logging obligation
        StringBuilder policyBuilder = new StringBuilder(adminPolicy.getPolicyContent());
        addObligationToPolicy(policyBuilder, DomainUtil.LOGGING_OBLIGATION);

        return new DomainPolicy(adminPolicy.getPolicyName(), policyBuilder.toString(), adminPolicy.getFileName());
    }

    private List<DomainPolicy> generateInternalPolicies(Department department, DomainResource departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        if (department.isInternalAccessUnrestricted()) {
            policies.add(generateUnrestrictedPolicy(departmentResource, department.getDepartmentRoles()));
        } else {
            policies.add(generateActionSpecificPolicy(departmentResource, department.getDepartmentInternalActions(),
                    department.getDepartmentRoles()));
        }

        return policies;
    }

    private List<DomainPolicy> generatePublicPolicies(Department department, DomainResource departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        if (department.isPublicAccessUnrestricted()) {
            policies.add(generateUnrestrictedPolicy(departmentResource, department.getRolesForPublicActions()));
        } else {
            policies.add(generateActionSpecificPolicy(departmentResource, department.getDepartmentPublicActions(),
                    department.getRolesForPublicActions()));

            //EXTENDED ROLES(with body/obligation/advice/transformation)
            policies.addAll(department.getExtendedRolesForPublicActions().stream()
                    .map(extendedRole ->
                            generateActionSpecificExtendedPolicy(departmentResource,
                                    department.getDepartmentPublicActions(), extendedRole))
                    .collect(Collectors.toList()));
        }

        return policies;
    }

    private List<DomainPolicy> generateSpecialPolicies(Department department, DomainResource departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        policies.add(generateActionSpecificPolicy(departmentResource, department.getDepartmentSpecialActions(),
                department.getRolesForSpecialActions()));

        //EXTENDED ROLES(with body/obligation/advice/transformation)
        policies.addAll(department.getExtendedRolesForSpecialActions().stream()
                .map(extendedRole ->
                        generateActionSpecificExtendedPolicy(departmentResource, department
                                .getDepartmentSpecialActions(), extendedRole))
                .collect(Collectors.toList()));

        return policies;
    }


    private DomainPolicy generateUnrestrictedPolicy(DomainResource resource, List<DomainRole> roles) {
        String policyName = String.format("%s full access on %s",
                DomainUtil.getRoleNames(roles), resource.getResourceName());

        String fileName = String.format("%03d-%s-%s", DomainUtil.getInt(), resource.getResourceName(),
                DomainUtil.getRoleNames(roles)).replaceAll("\\[|\\]", "")
                .replace(",", "_");

        StringBuilder policyBuilder = generateBasePolicy(policyName, resource);
        addRolesToPolicy(policyBuilder, roles);

        return new DomainPolicy(policyName, policyBuilder.toString(), fileName);
    }

    private DomainPolicy generateActionSpecificPolicy(DomainResource resource, List<String> actions,
                                                      List<DomainRole> roles) {
        String policyName = String.format("%s can perform %s on %s",
                DomainUtil.getRoleNames(roles), actions, resource.getResourceName());

        String fileName = String.format("%03d-%s-%s", DomainUtil.getInt(), resource.getResourceName(),
                DomainUtil.getRoleNames(roles)).replaceAll("\\[|\\]", "")
                .replace(",", "_");


        return new DomainPolicy(policyName, generateBasePolicyWithActions(policyName, resource, actions, roles)
                .toString(), fileName);
    }


    private DomainPolicy generateActionSpecificExtendedPolicy(DomainResource resource, List<String> actions,
                                                              ExtendedDomainRole extendedRole) {
        String policyName = String.format("[%s] can perform %s on %s (extended: %s)",
                extendedRole.getRole().getRoleName(), actions, resource.getResourceName(),
                DomainUtil.getExtendedRoleIndicator(extendedRole));

        String fileName = String.format("%03d-%s-%s", DomainUtil.getInt(), resource.getResourceName(),
                extendedRole.getRole().getRoleName())
                .replace(",", "_");

        StringBuilder policyBuilder = generateBasePolicyWithActions(policyName, resource, actions,
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

        return new DomainPolicy(policyName, policyBuilder.toString(), fileName);
    }

    private StringBuilder generateBasePolicy(String policyName, DomainResource resource) {
        return new StringBuilder().append("POLICY \"").append(policyName).append("\"")
                .append(System.lineSeparator())
                .append("PERMIT ")
                .append(String.format("(resource == %s)", resource.getResourceName()));
    }

    private StringBuilder generateBasePolicyWithActions(String policyName, DomainResource resource,
                                                        List<String> actions, List<DomainRole> roles) {
        StringBuilder policyBuilder = generateBasePolicy(policyName, resource);
        addRolesToPolicy(policyBuilder, roles);
        addActionsToPolicy(policyBuilder, actions);

        return policyBuilder;
    }

    private void addRolesToPolicy(StringBuilder policyBuilder, List<DomainRole> roles) {
        if (roles.isEmpty()) return;

        policyBuilder.append(System.lineSeparator()).append(TAB_STRING).append(" && ").append("(");
        boolean firstRole = true;
        for (DomainRole role : roles) {
            if (firstRole) firstRole = false;
            else policyBuilder.append(" || ");
            policyBuilder.append(String.format("(\"%s\" in subject..authority)", role.getRoleName()));
        }
        policyBuilder.append(")");
    }

    private void addActionsToPolicy(StringBuilder policyBuilder, List<String> actions) {
        if (actions.isEmpty()) return;

        policyBuilder.append(System.lineSeparator()).append(TAB_STRING).append(" && ").append("(");
        boolean firstAction = true;
        for (String action : actions) {
            if (firstAction) firstAction = false;
            else policyBuilder.append(" || ");
            policyBuilder.append(String.format("action == \"%s\"", action));
        }
        policyBuilder.append(")");
    }

    private void addBodyToPolicy(StringBuilder policyBuilder, DomainPolicyBody body) {
        policyBuilder.append(System.lineSeparator())
                .append("WHERE").append(System.lineSeparator())
                .append(TAB_STRING).append(body.getBody());
    }

    private void addObligationToPolicy(StringBuilder policyBuilder, DomainPolicyObligation obligation) {
        policyBuilder.append(System.lineSeparator())
                .append("OBLIGATION").append(System.lineSeparator())
                .append(TAB_STRING).append(obligation.getObligation());
    }

    private void addAdviceToPolicy(StringBuilder policyBuilder, DomainPolicyAdvice advice) {
        policyBuilder.append(System.lineSeparator())
                .append("ADVICE").append(System.lineSeparator())
                .append(TAB_STRING).append(advice.getAdvice());
    }

    private void addTransformationToPolicy(StringBuilder policyBuilder, DomainPolicyTransformation transformation) {
        policyBuilder.append(System.lineSeparator())
                .append("TRANSFORMATION").append(System.lineSeparator())
                .append(TAB_STRING).append(transformation.getTransformation());
    }
}
