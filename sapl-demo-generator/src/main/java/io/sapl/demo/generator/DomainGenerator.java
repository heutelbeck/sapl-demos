package io.sapl.demo.generator;

import io.sapl.demo.generator.example.Department;
import io.sapl.demo.generator.example.ExampleProvider;
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

    private final ExampleProvider domainRoleProvider;

    private final DomainParameter domainParameter;

    private final GeneratorUtility generatorUtility;

    private final List<DomainPolicy> domainPolicies = new ArrayList<>();

    public void generateDomainPolicies() {

        for (Department department : domainParameter.getDepartmentList()) {
            domainPolicies.addAll(generatePoliciesForDepartment(department));
        }

        LOGGER.info("generated {} policies", domainPolicies.size());
        generatorUtility.writeDomainPoliciesToFilesystem(domainPolicies);
    }


    private List<DomainPolicy> generatePoliciesForDepartment(Department department) {
        LOGGER.info("generating policies for department: {}", department.getDepartmentName());
        List<DomainPolicy> policiesForDepartment = new ArrayList<>();

        for (String departmentResource : department.getDepartmentResources()) {
            policiesForDepartment.add(generateSystemPolicyForResource(departmentResource)); //SYSTEM
            policiesForDepartment.add(generateAdministratorPolicyForResource(departmentResource)); //ADMIN

            policiesForDepartment.addAll(generatePoliciesForInternal(department, departmentResource));  //INTERNAL
            policiesForDepartment.addAll(generatePoliciesForPublic(department, departmentResource)); //PUBLIC
            policiesForDepartment.addAll(generatePoliciesForSpecialActions(department, departmentResource)); //SPECIAL
        }

        return policiesForDepartment;
    }

    private List<DomainPolicy> generatePoliciesForInternal(Department department, String departmentResource) {
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

    private List<DomainPolicy> generatePoliciesForPublic(Department department, String departmentResource) {
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

    private List<DomainPolicy> generatePoliciesForSpecialActions(Department department, String departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        policies.add(generateActionSpecificResourceAccessPolicyForRoles(departmentResource, department
                .getDepartmentSpecialActions(), department.getRolesForSpecialActions()));

        return policies;
    }


    private DomainPolicy generateSystemPolicyForResource(String resourceName) {
        return generateUnrestrictedResourceAccessPolicyForRoles(resourceName,
                Collections.singletonList(DomainRole.ROLE_SYSTEM.getRoleName()));
    }

    private DomainPolicy generateAdministratorPolicyForResource(String resourceName) {
        return generateUnrestrictedResourceAccessPolicyForRoles(resourceName,
                Collections.singletonList(DomainRole.ROLE_ADMIN.getRoleName()));
    }

    private StringBuilder generateBasePolicyStringForResource(String policyName, String resourceName) {
        return new StringBuilder().append("policy \"").append(policyName).append("\"")
                .append(System.lineSeparator())
                .append("permit ")
                .append(String.format("(resource == %s)", resourceName));
    }

    private DomainPolicy generateUnrestrictedResourceAccessPolicyForRoles(String resourceName,
                                                                          List<String> subjectRoles) {
        String policyName = String.format("%s full access on $s", subjectRoles, resourceName);

        StringBuilder policyBuilder = generateBasePolicyStringForResource(policyName, resourceName);
        addSubjectRolesToTargetExpression(policyBuilder, subjectRoles);

        return new DomainPolicy(policyName, policyBuilder.toString());
    }

    private DomainPolicy generateActionSpecificResourceAccessPolicyForRoles(String resourceName, List<String> actions,
                                                                            List<String> subjectRoles) {
        String policyName = String.format("%s can perform %s on %s", subjectRoles, actions, resourceName);

        StringBuilder policyBuilder = generateBasePolicyStringForResource(policyName, resourceName);
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

    private void addSubjectRolesToTargetExpression(StringBuilder policyBuilder, List<String> subjectRoles) {
        if (subjectRoles.isEmpty()) return;

        policyBuilder.append(System.lineSeparator()).append(" && ").append("(");
        boolean firstRole = true;
        for (String subjectRole : subjectRoles) {
            if (firstRole) firstRole = false;
            else policyBuilder.append(" || ");
            policyBuilder.append(generateRoleString(subjectRole));
        }
        policyBuilder.append(")");
    }

    private String generateRoleString(String role) {
        return String.format("(\"%s\" in subject..authority)", role);
    }

}
