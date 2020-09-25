package io.sapl.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.prp.ParsedDocumentIndex;
import io.sapl.api.prp.PolicyRetrievalResult;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType;
import io.sapl.prp.filesystem.FilesystemPolicyRetrievalPoint;
import io.sapl.prp.inmemory.indexed.FastParsedDocumentIndex;
import io.sapl.prp.inmemory.simple.SimpleParsedDocumentIndex;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestRunner {

    private static final double MILLION = 1000000.0D;

    private ParsedDocumentIndex getDocumentIndex(EmbeddedPolicyDecisionPoint.Builder.IndexType indexType) {
        switch (indexType) {
            case SIMPLE:
                return new SimpleParsedDocumentIndex();
            case FAST:
                return new FastParsedDocumentIndex(new AnnotationFunctionContext());
            default:
                return new SimpleParsedDocumentIndex();
        }
    }

    private FilesystemPolicyRetrievalPoint createPRP(String policyFolder, IndexType indexType) {
        ParsedDocumentIndex index = this.getDocumentIndex(indexType);
        return new FilesystemPolicyRetrievalPoint(policyFolder, getDocumentIndex(indexType));
    }

    public List<XlsRecord> runTestNew(PolicyGeneratorConfiguration config, String policyFolder,
                                      BenchmarkDataContainer benchmarkDataContainer) throws Exception {

        PolicyGenerator generator = new PolicyGenerator(config);

        return run(config, policyFolder, benchmarkDataContainer, generator);
    }

    private List<XlsRecord> run(PolicyGeneratorConfiguration config, String policyFolder,
                                BenchmarkDataContainer benchmarkDataContainer, PolicyGenerator generator) {

        List<XlsRecord> results = new LinkedList<>();

        LOGGER.info("running benchmark with config={}, runs={}", config.getName(), benchmarkDataContainer.getRuns());

        try {
            Map<String, JsonNode> variables = Collections.emptyMap();

            //create PRP
            long begin = System.nanoTime();
            FilesystemPolicyRetrievalPoint policyRetrievalPoint =
                    createPRP(policyFolder, benchmarkDataContainer.getIndexType());
            double timePreparation = nanoToMs(System.nanoTime() - begin);


            for (int j = 0; j < benchmarkDataContainer.getRuns(); j++) {
                AuthorizationSubscription request = generator.createEmptySubscription();

                long start = System.nanoTime();
                PolicyRetrievalResult result =
                        policyRetrievalPoint.retrievePolicies(request, new AnnotationFunctionContext(), variables)
                                .blockFirst();
                long end = System.nanoTime();

                double timeRetrieve = nanoToMs(end - start);

                results.add(new XlsRecord(j, config.getName(), timePreparation, timeRetrieve, request.toString(),
                        buildResponseStringForResult(result)));

                LOGGER.debug("Total : {}ms", timeRetrieve);
            }

        } catch (Exception e) {
            LOGGER.error("Error running test", e);
        }

        return results;
    }

    private String buildResponseStringForResult(PolicyRetrievalResult policyRetrievalResult) {
        return String.format("PolicyRetrievalResult(matchingDocumentsCount=%d, errorsInTarget=%b)",
                policyRetrievalResult.getMatchingDocuments().size(), policyRetrievalResult.isErrorsInTarget());
    }

    public List<XlsRecord> runTest(PolicyGeneratorConfiguration config, String path, boolean reuseExistingPolicies,
                                   BenchmarkDataContainer benchmarkDataContainer) throws Exception {

        PolicyGenerator generator = new PolicyGenerator(config);
        String subfolder = generateRandomPolicies(generator, path, reuseExistingPolicies);

        List<XlsRecord> results = new LinkedList<>();


        runTestNew(config, subfolder, benchmarkDataContainer);
//
//        try {
//            for (int i = 0; i < benchmarkDataContainer.getIterations(); i++) {
//                long begin = System.nanoTime();
//                PolicyDecisionPoint pdp = EmbeddedPolicyDecisionPoint.builder()
//                        .withFilesystemPolicyRetrievalPoint(path + subfolder,
//                                benchmarkDataContainer.getIndexType())
//                        .withResourcePDPConfigurationProvider("/policies")
////                        .withFilesystemPDPConfigurationProvider(path + subfolder)
//                        .build();
//                double prep = nanoToMs(System.nanoTime() - begin);
//
//                for (int j = 0; j < benchmarkDataContainer.getRuns(); j++) {
//                    AuthorizationSubscription request = generator.createSubscriptionWithAllVariables();
//                    LOGGER.trace("{}", new Gson().toJson(request));
//
//                    long start = System.nanoTime();
//                    AuthorizationDecision authzDecision = pdp.decide(request).blockFirst();
//                    long end = System.nanoTime();
//
//                    double diff = nanoToMs(end - start);
//
//                    if (authzDecision == null) {
//                        throw new IOException("PDP returned null authzDecision");
//                    }
//                    results.add(new XlsRecord(j + (i * benchmarkDataContainer.getRuns()), config.getName(),
//                            prep, diff, request.toString(), authzDecision.toString()));
//
//                    LOGGER.debug("Total : {}ms", diff);
//                }
//            }
//        } catch (IOException | AttributeException | FunctionException | PDPConfigurationException e) {
//            LOGGER.error("Error running test", e);
//        }

        return results;
    }

    private String generateRandomPolicies(PolicyGenerator generator, String path,
                                          boolean reuseExistingPolicies) throws IOException {


        String subfolder = generator.getConfig().getName().replaceAll("[^a-zA-Z0-9]", "");
        if (!reuseExistingPolicies) {
            generator.generatePolicies(subfolder);

            final Path dir = Paths.get(path, subfolder);
            Files.createDirectories(dir);
//            Files.copy(Paths.get(path, "pdp.json"), Paths.get(path, subfolder, "pdp.json"),
//                    StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        }
        return subfolder;
    }


    private double nanoToMs(long nanoseconds) {
        return nanoseconds / MILLION;
    }
}
