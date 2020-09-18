package io.sapl.demo.generator;

import io.sapl.demo.generator.DomainPolicy.DomainPolicyBody;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyObligation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class DomainUtil {

    private final String policyPath;

    public static final DomainPolicyObligation LOGGING_OBLIGATION =
            new DomainPolicyObligation("\"logging:log_access\"");
    public static final DomainPolicyBody TREATING_BODY = new DomainPolicyBody("subject in resource.<patient.treating>");
    public static final DomainPolicyBody RELATIVE_BODY = new DomainPolicyBody("subject in resource.<patient.relatives>");
    public static final DomainPolicyBody OWN_DATA_BODY = new DomainPolicyBody("subject.id == resource.patient");


    public void writeDomainPoliciesToFilesystem(List<DomainPolicy> domainPolicies) {
        for (DomainPolicy domainPolicy : domainPolicies) {
            writePolicyToFile(domainPolicy);
        }
    }

    public void printDomainPoliciesLimited(List<DomainPolicy> domainPolicies) {
        LOGGER.debug("#################### POLICIES ####################");
        for (DomainPolicy domainPolicy : domainPolicies) {
            LOGGER.debug("--------------------------------------------------");
            LOGGER.debug(domainPolicy.getPolicyContent());
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

    public static <T> List<T> combineList(List<T> listOne, List<T> listTwo) {
        return Stream.concat(listOne.stream(), listTwo.stream())
                .collect(Collectors.toList());
    }


}
