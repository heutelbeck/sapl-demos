package io.sapl.demo.generator;

import io.sapl.demo.generator.DomainPolicy.DomainPolicyAdvice;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyBody;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyObligation;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyTransformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class DomainRole {

    private final String roleName;

    public static class DomainRoles {

        public static DomainRole ROLE_AUTHORIZED = new DomainRole("authorized");
        public static DomainRole ROLE_ADMIN = new DomainRole("admin");
        public static DomainRole ROLE_SYSTEM = new DomainRole("system");

        public static DomainRole findByName(List<DomainRole> roleList, String roleName) {
            return roleList.stream()
                    .filter(domainRole -> domainRole.getRoleName().equalsIgnoreCase(roleName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("missing role: " + roleName));
        }

        public static List<DomainRole> toRole(List<ExtendedDomainRole> rolesForAction) {
            return rolesForAction.stream().map(ExtendedDomainRole::getRole).collect(Collectors.toList());
        }

    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExtendedDomainRole {

        private DomainRole role;

        @Builder.Default
        private DomainPolicyBody body = null;
        @Builder.Default
        private DomainPolicyObligation obligation = null;
        @Builder.Default
        private DomainPolicyAdvice advice = null;
        @Builder.Default
        private DomainPolicyTransformation transformation = null;


        public ExtendedDomainRole(DomainRole role) {
            this.role = role;
        }

        public boolean isBodyPresent() {

            return this.body != null;
        }

        public boolean isObligationPresent() {
            return this.obligation != null;
        }

        public boolean isAdvicePresent() {
            return this.advice != null;
        }

        public boolean isTransformationPresent() {
            return this.transformation != null;
        }


    }

}
