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

    private final DomainData domainData;

    private final DomainUtil domainUtil;


    public void generateDomainPolicies() {
        // HOSPITAL
        List<DomainPolicy> domainPolicies = generatePoliciesForHospital(domainData.getHospital());

        //DEPARTMENTS
        for (Department department : domainData.getDepartments()) {
            domainPolicies.addAll(generatePoliciesForDepartment(department));
        }

        LOGGER.info("generated {} policies", domainPolicies.size());

        domainUtil.printDomainPoliciesLimited(domainPolicies.stream().limit(10).collect(Collectors.toList()));
//        domainUtil.writeDomainPoliciesToFilesystem(domainPolicies);
    }

    private List<DomainPolicy> generatePoliciesForHospital(Hospital hospital) {
        LOGGER.info("generating policies for hospital");
        List<DomainPolicy> policiesForHospital = new ArrayList<>();

        for (DomainResource hospitalResource : hospital.getHospitalResources()) {
            /* GENERAL */
            policiesForHospital.addAll(generatePoliciesForGeneral(hospital, hospitalResource));

            /* RESOURCE SPECIFIC */
            policiesForHospital.addAll(generatePoliciesForResource(hospital, hospitalResource));
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

        return policies;
    }

    private List<DomainPolicy> generatePoliciesForResource(Hospital hospital, DomainResource hospitalResource) {
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

        return policies;
    }


    private List<DomainPolicy> generatePoliciesForDepartment(Department department) {
        LOGGER.info("generating policies for department: {}", department.getDepartmentName());
        List<DomainPolicy> policiesForDepartment = new ArrayList<>();

        for (DomainResource departmentResource : department.getDepartmentResources()) {
            policiesForDepartment.add(generateSystemPolicy(departmentResource)); //SYSTEM
            policiesForDepartment.add(generateAdministratorPolicy(departmentResource)); //ADMIN

            policiesForDepartment.addAll(generateInternalPolicies(department, departmentResource));  //INTERNAL
            policiesForDepartment.addAll(generatePublicPolicies(department, departmentResource)); //PUBLIC
            policiesForDepartment.addAll(generateSpecialPolicies(department, departmentResource)); //SPECIAL
        }

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

        return new DomainPolicy(adminPolicy.getPolicyName(), policyBuilder.toString());
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


    private DomainPolicy generateUnrestrictedPolicy(DomainResource resource, List<DomainRole> subjectRoles) {
        String policyName = String.format("%s full access on %s", subjectRoles, resource);

        StringBuilder policyBuilder = generateBasePolicy(policyName, resource);
        addRolesToPolicy(policyBuilder, subjectRoles);

        return new DomainPolicy(policyName, policyBuilder.toString());
    }

    private DomainPolicy generateActionSpecificPolicy(DomainResource resource, List<String> actions,
                                                      List<DomainRole> roles) {
        String policyName = String.format("%s can perform %s on %s", roles, actions, resource);

        return new DomainPolicy(policyName, generateBasePolicyWithActions(policyName, resource, actions, roles)
                .toString());
    }


    private DomainPolicy generateActionSpecificExtendedPolicy(DomainResource resource, List<String> actions,
                                                              ExtendedDomainRole extendedRole) {
        String policyName = String.format("%s can perform %s on %s - extended", extendedRole, actions, resource);

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

        return new DomainPolicy(policyName, policyBuilder.toString());
    }

    private StringBuilder generateBasePolicy(String policyName, DomainResource resource) {
        return new StringBuilder().append("policy \"").append(policyName).append("\"")
                .append(System.lineSeparator())
                .append("permit ")
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

        policyBuilder.append(System.lineSeparator()).append(" && ").append("(");
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

        policyBuilder.append(System.lineSeparator()).append(" && ").append("(");
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
