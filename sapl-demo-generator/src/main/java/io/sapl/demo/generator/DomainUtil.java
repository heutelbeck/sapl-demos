package io.sapl.demo.generator;

import io.sapl.demo.generator.DomainPolicy.DomainPolicyBody;
import io.sapl.demo.generator.DomainPolicy.DomainPolicyObligation;
import io.sapl.demo.generator.DomainRole.ExtendedDomainRole;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DomainUtil {

    private static final AtomicInteger policyCounter = new AtomicInteger();
    private static final AtomicInteger roleCounter = new AtomicInteger();
    private static final AtomicInteger resourceCounter = new AtomicInteger();
    private static final AtomicInteger appendixCounter = new AtomicInteger();

    private final String policyPath;
    private final boolean cleanDirectory;

    public static final DomainPolicyObligation LOG_OBLIGATION = new DomainPolicyObligation("\"logging:log_access\"");
    public static final DomainPolicyBody TREATING_BODY = new DomainPolicyBody("subject in resource.<patient.treating>");
    public static final DomainPolicyBody RELATIVE_BODY = new DomainPolicyBody("subject in resource.<patient.relatives>");
    public static final DomainPolicyBody OWN_DATA_BODY = new DomainPolicyBody("subject.id == resource.patient");

    private static final Random dice = new Random();


    public void writeDomainPoliciesToFilesystem(List<DomainPolicy> domainPolicies) {
        if (cleanDirectory) cleanPolicyDirectory();

        for (DomainPolicy domainPolicy : domainPolicies) {
            writePolicyToFile(domainPolicy);
        }
    }

    @SneakyThrows
    public void cleanPolicyDirectory() {
        LOGGER.debug("removing existing policies in output directory");
        FileUtils.cleanDirectory(new File(policyPath));
    }

    public void printDomainPoliciesLimited(List<DomainPolicy> domainPolicies) {
        LOGGER.trace("#################### POLICIES ####################");
        for (DomainPolicy domainPolicy : domainPolicies) {
            LOGGER.trace("{}--------------------------------------------------{}{}{}--------------------------------------------------",
                    System.lineSeparator(), System.lineSeparator(), domainPolicy.getPolicyContent(), System
                            .lineSeparator());
        }
    }

    public void writePolicyToFile(DomainPolicy policy) {
        String policyFileName = String
                .format("%s/%03d_%s.sapl", policyPath, DomainUtil.getNextPolicyCount(), policy.getFileName());
        LOGGER.trace("writing policy file: {}", policyFileName);

        try (PrintWriter writer = new PrintWriter(policyFileName, StandardCharsets.UTF_8.name())) {
            writer.println(policy.getPolicyContent());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            LOGGER.error("writing policy file failed", e);
        }
    }

    public void writeConfigurationInfoToFile(String settingsString) {
        String fileName = policyPath + "/configurationInfo.txt";
        LOGGER.debug("writing config info file: {}", fileName);

        try (PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8.name())) {
            writer.println(settingsString);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            LOGGER.error("writing config info file failed", e);
        }
    }


    public static List<String> getRoleNames(List<DomainRole> roles) {
        return roles.stream().map(DomainRole::getRoleName).collect(Collectors.toList());
    }

    public static List<String> getResourceNames(List<DomainResource> resources) {
        return resources.stream().map(DomainResource::getResourceName).collect(Collectors.toList());
    }

    public static String getResourcesStringForFileName(List<DomainResource> resources) {
        String firstResourceName = resources.get(0).getResourceName();
        if (resources.size() < 2) return firstResourceName;
        if (!firstResourceName.contains(".")) return firstResourceName;

        String[] split = firstResourceName.split("\\.");
        return String.format("%ss.%s", split[0], split[1]);
    }

    public static List<String> getExtendedRoleNames(List<ExtendedDomainRole> roles) {
        return roles.stream().map(ExtendedDomainRole::getRole).map(DomainRole::getRoleName)
                .collect(Collectors.toList());
    }

    public static String getExtendedRoleIndicator(ExtendedDomainRole role) {
        return String.format("b=%d,o=%d,a=%d,t=%d",
                role.isBodyPresent() ? 1 : 0, role.isObligationPresent() ? 1 : 0, role.isAdvicePresent() ? 1 : 0, role
                        .isTransformationPresent() ? 1 : 0);
    }


    public static int getNextPolicyCount() {
        return policyCounter.getAndIncrement();
    }

    public static int getNextRoleCount() {
        return roleCounter.getAndIncrement();
    }

    public static int getNextResourceCount() {
        return resourceCounter.getAndIncrement();
    }

    public static int getNextAppendixCount() {
        return appendixCounter.getAndIncrement();
    }

    public static String sanitizeFileName(String fileName) {
        return fileName.toLowerCase().replaceAll("\\.", "-")
                .replaceAll("[\\[\\]]", "").replace(", ", "-");
    }

    public static String getIOrDefault(List<String> list, int i, String defaultStr) {
        try {
            return list.get(i);
        } catch (Exception e) {
            return defaultStr;
        }
    }

    public static boolean rollIsLowerThanProbability(double probability) {
        return dice.nextDouble() < probability;
    }
}
