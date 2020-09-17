package io.sapl.demo.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GeneratorUtility {

    private final String policyPath;

    private static final String DUMMY_OBLIGATION = "{\"type\":\"logAccess\"}";


    public void writeDomainPoliciesToFilesystem(List<DomainPolicy> domainPolicies) {
        for (DomainPolicy domainPolicy : domainPolicies) {
            writePolicyToFile(domainPolicy);
        }
    }

    public void writePolicyToFile(DomainPolicy policy) {
        String policyFileName = policyPath + policy.getPolicyName() + ".sapl";
        LOGGER.info("writing policy file: {}", policyFileName);

        try (PrintWriter writer = new PrintWriter(policyFileName, StandardCharsets.UTF_8.name())) {
            writer.println(policy.getPolicyContent());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            LOGGER.error("writing policy file failed", e);
        }
    }

    public String appendObligationToPolicy(String policy) {
        return policy + System.lineSeparator() +
                "obligation" + System.lineSeparator() + DUMMY_OBLIGATION;

    }

}
