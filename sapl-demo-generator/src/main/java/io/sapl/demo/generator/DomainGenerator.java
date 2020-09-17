package io.sapl.demo.generator;

import io.sapl.demo.generator.example.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainGenerator {

    private final DomainData domainData;

    private final GeneratorUtility generatorUtility;

    private final List<DomainPolicy> domainPolicies = new ArrayList<>();

    public void generateDomainPolicies() {

        for (Department department : domainData.getDepartments()) {
            domainPolicies.addAll(generatePoliciesForDepartment(department));
        }

        LOGGER.info("generated {} policies", domainPolicies.size());
        generatorUtility.writeDomainPoliciesToFilesystem(domainPolicies);
    }


    private List<DomainPolicy> generatePoliciesForDepartment(Department department) {
        LOGGER.info("generating policies for department: {}", department.getDepartmentName());
        List<DomainPolicy> policiesForDepartment = new ArrayList<>();

        for (DomainResource departmentResource : department.getDepartmentResources()) {
            policiesForDepartment.add(generateSystemPolicyForResource(departmentResource)); //SYSTEM
            policiesForDepartment.add(generateAdministratorPolicyForResource(departmentResource)); //ADMIN

            policiesForDepartment.addAll(generatePoliciesForInternal(department, departmentResource));  //INTERNAL
            policiesForDepartment.addAll(generatePoliciesForPublic(department, departmentResource)); //PUBLIC
            policiesForDepartment.addAll(generatePoliciesForSpecialActions(department, departmentResource)); //SPECIAL
        }

        return policiesForDepartment;
    }

    private List<DomainPolicy> generatePoliciesForInternal(Department department, DomainResource departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        if (department.isInternalAccessUnrestricted()) {
            policies.add(generateUnrestrictedResourceAccessPolicyForRoles(departmentResource, department
                    .getDepartmentRoles()));
        } else {
            policies.add(generateActionSpecificResourceAccessPolicyForRoles(departmentResource, department
                    .getDepartmentInternalActions(), department.getDepartmentRoles()));
        }

        return policies;
    }

    private List<DomainPolicy> generatePoliciesForPublic(Department department, DomainResource departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        if (department.isPublicAccessUnrestricted()) {
            policies.add(generateUnrestrictedResourceAccessPolicyForRoles(departmentResource, department
                    .getRolesForPublicActions()));
        } else {
            policies.add(generateActionSpecificResourceAccessPolicyForRoles(departmentResource, department
                    .getDepartmentPublicActions(), department.getRolesForPublicActions()));
        }

        return policies;
    }

    private List<DomainPolicy> generatePoliciesForSpecialActions(Department department,
                                                                 DomainResource departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        policies.add(generateActionSpecificResourceAccessPolicyForRoles(departmentResource, department
                .getDepartmentSpecialActions(), department.getRolesForSpecialActions()));

        return policies;
    }

    private DomainPolicy generateSystemPolicyForResource(DomainResource resource) {
        return generateUnrestrictedResourceAccessPolicyForRoles(resource,
                Collections.singletonList(DomainRole.ROLE_SYSTEM));
    }

    private DomainPolicy generateAdministratorPolicyForResource(DomainResource resource) {
        return generateUnrestrictedResourceAccessPolicyForRoles(resource,
                Collections.singletonList(DomainRole.ROLE_ADMIN));
    }

    private StringBuilder generateBasePolicyStringForResource(String policyName, DomainResource resource) {
        return new StringBuilder().append("policy \"").append(policyName).append("\"")
                .append(System.lineSeparator())
                .append("permit ")
                .append(String.format("(resource == %s)", resource.getResourceName()));
    }

    private DomainPolicy generateUnrestrictedResourceAccessPolicyForRoles(DomainResource resource,
                                                                          List<DomainRole> subjectRoles) {
        String policyName = String.format("%s full access on %s", subjectRoles, resource);

        StringBuilder policyBuilder = generateBasePolicyStringForResource(policyName, resource);
        addSubjectRolesToTargetExpression(policyBuilder, subjectRoles);

        return new DomainPolicy(policyName, policyBuilder.toString());
    }

    private DomainPolicy generateActionSpecificResourceAccessPolicyForRoles(DomainResource resource,
                                                                            List<String> actions,
                                                                            List<DomainRole> subjectRoles) {
        String policyName = String.format("%s can perform %s on %s", subjectRoles, actions, resource);

        StringBuilder policyBuilder = generateBasePolicyStringForResource(policyName, resource);
        addSubjectRolesToTargetExpression(policyBuilder, subjectRoles);
        addActionsToTargetExpression(policyBuilder, actions);

        return new DomainPolicy(policyName, policyBuilder.toString());
    }

    private void addActionsToTargetExpression(StringBuilder policyBuilder, List<String> actions) {
        if (actions.isEmpty()) return;

        policyBuilder.append(System.lineSeparator()).append(" && ").append("(");
        boolean firstAction = true;
        for (String action : actions) {
            if (firstAction) firstAction = false;
            else policyBuilder.append(" || ");
            policyBuilder.append(generateActionString(action));
        }
        policyBuilder.append(")");
    }

    private String generateActionString(String action) {
        return String.format("action == \"%s\"", action);
    }

    private void addSubjectRolesToTargetExpression(StringBuilder policyBuilder, List<DomainRole> subjectRoles) {
        if (subjectRoles.isEmpty()) return;

        policyBuilder.append(System.lineSeparator()).append(" && ").append("(");
        boolean firstRole = true;
        for (DomainRole subjectRole : subjectRoles) {
            if (firstRole) firstRole = false;
            else policyBuilder.append(" || ");
            policyBuilder.append(generateRoleString(subjectRole));
        }
        policyBuilder.append(")");
    }

    private String generateRoleString(DomainRole role) {
        return String.format("(\"%s\" in subject..authority)", role.getRoleName());
    }

}
