package io.sapl.demo.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DomainRole {

    public static DomainRole ROLE_ADMIN = new DomainRole("ROLE_ADMIN");
    public static DomainRole ROLE_SYSTEM = new DomainRole("ROLE_SYSTEM");

    private final String roleName;



}
