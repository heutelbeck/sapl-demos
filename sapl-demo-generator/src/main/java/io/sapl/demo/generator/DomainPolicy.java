package io.sapl.demo.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DomainPolicy {

    private final String policyName;

    private final String policyContent;
}
