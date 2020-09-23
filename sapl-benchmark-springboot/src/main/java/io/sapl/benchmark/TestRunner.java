package io.sapl.benchmark;

import io.sapl.api.functions.FunctionException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PDPConfigurationException;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pip.AttributeException;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class TestRunner {

    private static final double MILLION = 1000000.0D;


    public List<XlsRecord> runTestNew(PolicyGeneratorConfiguration config, String policyFolder,
                                      BenchmarkDataContainer benchmarkDataContainer) throws Exception {

        PolicyGenerator generator = new PolicyGenerator(config);

        List<XlsRecord> results = new LinkedList<>();

        try {
            for (int i = 0; i < benchmarkDataContainer.getIterations(); i++) {
                long begin = System.nanoTime();
                PolicyDecisionPoint pdp = EmbeddedPolicyDecisionPoint.builder()
                        .withFilesystemPolicyRetrievalPoint(policyFolder, benchmarkDataContainer.getIndexType())
                        .withFilesystemPDPConfigurationProvider(policyFolder).build();
                double prep = nanoToMs(System.nanoTime() - begin);

                for (int j = 0; j < benchmarkDataContainer.getRuns(); j++) {
                    AuthorizationSubscription request = generator.createSubscriptionWithAllVariables();

                    long start = System.nanoTime();
                    AuthorizationDecision authzDecision = pdp.decide(request).blockFirst();
                    long end = System.nanoTime();

                    double diff = nanoToMs(end - start);

                    if (authzDecision == null) {
                        throw new IOException("PDP returned null authzDecision");
                    }
                    results.add(new XlsRecord(j + (i * benchmarkDataContainer.getRuns()), config.getName(),
                            prep, diff, request.toString(), authzDecision.toString()));

                    LOGGER.debug("Total : {}ms", diff);
                }
            }
        } catch (IOException | AttributeException | FunctionException | PDPConfigurationException e) {
            LOGGER.error("Error running test", e);
        }

        return results;
    }

    public List<XlsRecord> runTest(PolicyGeneratorConfiguration config, String path, boolean reuseExistingPolicies,
                                   BenchmarkDataContainer benchmarkDataContainer) throws Exception {

        PolicyGenerator generator = new PolicyGenerator(config);

        String subfolder = config.getName().replaceAll("[^a-zA-Z0-9]", "");
        if (!reuseExistingPolicies) {
            generator.generatePolicies(subfolder);

            final Path dir = Paths.get(path, subfolder);
            Files.createDirectories(dir);
//            Files.copy(Paths.get(path, "pdp.json"), Paths.get(path, subfolder, "pdp.json"),
//                    StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        }

        List<XlsRecord> results = new LinkedList<>();

        try {
            for (int i = 0; i < benchmarkDataContainer.getIterations(); i++) {
                long begin = System.nanoTime();
                PolicyDecisionPoint pdp = EmbeddedPolicyDecisionPoint.builder()
                        .withFilesystemPolicyRetrievalPoint(path + subfolder,
                                benchmarkDataContainer.getIndexType())
                        .withResourcePDPConfigurationProvider("/policies")
//                        .withFilesystemPDPConfigurationProvider(path + subfolder)
                        .build();
                double prep = nanoToMs(System.nanoTime() - begin);

                for (int j = 0; j < benchmarkDataContainer.getRuns(); j++) {
                    AuthorizationSubscription request = generator.createSubscriptionWithAllVariables();

                    long start = System.nanoTime();
                    AuthorizationDecision authzDecision = pdp.decide(request).blockFirst();
                    long end = System.nanoTime();

                    double diff = nanoToMs(end - start);

                    if (authzDecision == null) {
                        throw new IOException("PDP returned null authzDecision");
                    }
                    results.add(new XlsRecord(j + (i * benchmarkDataContainer.getRuns()), config.getName(),
                            prep, diff, request.toString(), authzDecision.toString()));

                    LOGGER.debug("Total : {}ms", diff);
                }
            }
        } catch (IOException | AttributeException | FunctionException | PDPConfigurationException e) {
            LOGGER.error("Error running test", e);
        }

        return results;
    }


    private double nanoToMs(long nanoseconds) {
        return nanoseconds / MILLION;
    }
}
