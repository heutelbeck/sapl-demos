package io.sapl.demo.generator;

import io.sapl.demo.generator.DomainPolicy.DomainPolicyAdvice;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyBody;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyObligation;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyTransformation;
import io.sapl.demo.generator.DomainRole.DomainRoles;
import io.sapl.demo.generator.DomainRole.ExtendedDomainRole;
import io.sapl.demo.generator.example.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainGenerator {

    private final DomainData domainData;

    private final DomainUtil domainUtil;

    private final List<DomainPolicy> domainPolicies = new ArrayList<>();

    public void generateDomainPolicies() {

        for (Department department : domainData.getDepartments()) {
            domainPolicies.addAll(generatePoliciesForDepartment(department));
        }

        LOGGER.info("generated {} policies", domainPolicies.size());

        domainUtil.printDomainPoliciesLimited(domainPolicies.stream().limit(10).collect(Collectors.toList()));
//        domainUtil.writeDomainPoliciesToFilesystem(domainPolicies);
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
            policies.add(generateUnrestrictedResourceAccessPolicyForRoles(departmentResource,
                    department.getRolesForPublicActions()));
        } else {
            policies.add(generateActionSpecificResourceAccessPolicyForRoles(departmentResource, department
                    .getDepartmentPublicActions(), department.getRolesForPublicActions()));

            //EXTENDED ROLES(with body/obligation/advice/transformation)
            policies.addAll(department.getExtendedRolesForPublicActions().stream()
                    .map(extendedRole ->
                            generateActionSpecificResourceAccessPolicyForExtendedRole(departmentResource,
                                    department.getDepartmentPublicActions(), extendedRole))
                    .collect(Collectors.toList()));
        }

        return policies;
    }

    private List<DomainPolicy> generatePoliciesForSpecialActions(Department department,
                                                                 DomainResource departmentResource) {
        List<DomainPolicy> policies = new ArrayList<>();

        policies.add(generateActionSpecificResourceAccessPolicyForRoles(departmentResource, department
                .getDepartmentSpecialActions(), department.getRolesForSpecialActions()));

        //EXTENDED ROLES(with body/obligation/advice/transformation)
        policies.addAll(department.getExtendedRolesForSpecialActions().stream()
                .map(extendedRole ->
                        generateActionSpecificResourceAccessPolicyForExtendedRole(departmentResource,
                                department.getDepartmentSpecialActions(), extendedRole))
                .collect(Collectors.toList()));

        return policies;
    }

    private DomainPolicy generateSystemPolicyForResource(DomainResource resource) {
        return generateUnrestrictedResourceAccessPolicyForRoles(resource,
                Collections.singletonList(DomainRoles.ROLE_SYSTEM));
    }

    private DomainPolicy generateAdministratorPolicyForResource(DomainResource resource) {
        DomainPolicy adminPolicy = generateUnrestrictedResourceAccessPolicyForRoles(resource,
                Collections.singletonList(DomainRoles.ROLE_ADMIN));
        //add logging obligation
        StringBuilder policyBuilder = new StringBuilder(adminPolicy.getPolicyContent());
        addObligationToPolicy(policyBuilder, DomainUtil.LOGGING_OBLIGATION);

        return new DomainPolicy(adminPolicy.getPolicyName(), policyBuilder.toString());
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
                                                                            List<DomainRole> roles) {
        String policyName = String.format("%s can perform %s on %s", roles, actions, resource);

        return new DomainPolicy(policyName,
                generateActionSpecificResourceAccessPolicyBase(policyName, resource, actions, roles).toString());
    }


    private DomainPolicy generateActionSpecificResourceAccessPolicyForExtendedRole(
            DomainResource resource,
            List<String> actions,
            ExtendedDomainRole extendedRole) {
        String policyName = String.format("%s can perform %s on %s - extended", extendedRole, actions, resource);

        StringBuilder policyBuilder =
                generateActionSpecificResourceAccessPolicyBase(policyName, resource, actions,
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

        return new DomainPolicy(policyName, policyBuilder.toString());
    }

    private StringBuilder generateActionSpecificResourceAccessPolicyBase(String policyName,
                                                                         DomainResource resource,
                                                                         List<String> actions,
                                                                         List<DomainRole> roles) {

        StringBuilder policyBuilder = generateBasePolicyStringForResource(policyName, resource);
        addSubjectRolesToTargetExpression(policyBuilder, roles);
        addActionsToTargetExpression(policyBuilder, actions);

        return policyBuilder;
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

    private void addBodyToPolicy(StringBuilder policyBuilder, DomainPolicyBody body) {
        policyBuilder.append(System.lineSeparator())
                .append("where").append(System.lineSeparator())
                .append(body.getBody());
    }

    private void addObligationToPolicy(StringBuilder policyBuilder, DomainPolicyObligation obligation) {
        policyBuilder.append(System.lineSeparator())
                .append("obligation").append(System.lineSeparator())
                .append(obligation.getObligation());

    }

    private void addAdviceToPolicy(StringBuilder policyBuilder, DomainPolicyAdvice advice) {
        policyBuilder.append(System.lineSeparator())
                .append("advice").append(System.lineSeparator())
                .append(advice.getAdvice());

    }

    private void addTransformationToPolicy(StringBuilder policyBuilder, DomainPolicyTransformation transformation) {
        policyBuilder.append(System.lineSeparator())
                .append("transformation").append(System.lineSeparator())
                .append(transformation.getTransformation());

    }

}
