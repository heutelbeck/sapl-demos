package io.sapl.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.analyzer.PolicyAnalyzer;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.interpreter.SAPLInterpreter;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.prp.ParsedDocumentIndex;
import io.sapl.api.prp.PolicyRetrievalResult;
import io.sapl.generator.DomainData;
import io.sapl.generator.DomainGenerator;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.DefaultSAPLInterpreter;
import io.sapl.interpreter.combinators.DenyUnlessPermitCombinator;
import io.sapl.interpreter.combinators.DocumentsCombinator;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType;
import io.sapl.prp.filesystem.FilesystemPolicyRetrievalPoint;
import io.sapl.prp.inmemory.indexed.FastParsedDocumentIndex;
import io.sapl.prp.inmemory.indexed.improved.ImprovedDocumentIndex;
import io.sapl.prp.inmemory.simple.SimpleParsedDocumentIndex;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class TestRunner {

    private static final double MILLION = 1000000.0D;

    private ParsedDocumentIndex getDocumentIndex(EmbeddedPolicyDecisionPoint.Builder.IndexType indexType) {
        switch (indexType) {
            case IMPROVED:
                return new ImprovedDocumentIndex(new AnnotationFunctionContext());
            case FAST:
                return new FastParsedDocumentIndex(new AnnotationFunctionContext());
            case SIMPLE:
                //fall through
            default:
                return new SimpleParsedDocumentIndex();
        }
    }

    private FilesystemPolicyRetrievalPoint createPRP(String policyFolder, IndexType indexType) {
        ParsedDocumentIndex documentIndex = getDocumentIndex(indexType);

        validateIndexImplementation(indexType, documentIndex);
        return new FilesystemPolicyRetrievalPoint(policyFolder, documentIndex);
    }

    private void validateIndexImplementation(IndexType indexType, ParsedDocumentIndex documentIndex) {
        switch (indexType) {
            case IMPROVED:
                if (!(documentIndex instanceof ImprovedDocumentIndex))
                    throw new RuntimeException(String
                            .format("wrong index impl. expected %s but got %s", indexType, documentIndex.getClass()));
                break;
            case FAST:
                if (!(documentIndex instanceof FastParsedDocumentIndex))
                    throw new RuntimeException(String
                            .format("wrong index impl. expected %s but got %s", indexType, documentIndex.getClass()));
                break;
            case SIMPLE:
                if (!(documentIndex instanceof SimpleParsedDocumentIndex))
                    throw new RuntimeException(String
                            .format("wrong index impl. expected %s but got %s", indexType, documentIndex.getClass()));
                break;
        }
    }

    public List<XlsRecord> runTest(PolicyGeneratorConfiguration config, String path,
                                   BenchmarkDataContainer benchmarkDataContainer,
                                   DomainGenerator domainGenerator) throws Exception {

        PolicyGenerator generator = new PolicyGenerator(config, domainGenerator.getDomainData());
        String subFolder = generateRandomPolicies(generator, path);

        List<XlsRecord> results = new LinkedList<>();

        runTestNew(config, subFolder, benchmarkDataContainer, domainGenerator);

        return results;
    }

    public List<XlsRecord> runTestNew(PolicyGeneratorConfiguration config, String policyFolder,
                                      BenchmarkDataContainer benchmarkDataContainer, DomainGenerator domainGenerator) {

        LOGGER.info("generating domain policies with seed {}", config.getSeed());
        domainGenerator.generateDomainPoliciesWithSeed(config.getSeed(),
                domainGenerator.getDomainData().getPolicyDirectoryPath());

        //update config by analyzing the generated policies
        config = new PolicyAnalyzer(domainGenerator.getDomainData())
                .analyzeSaplDocuments(benchmarkDataContainer.getIndexType());

        return run(config, policyFolder, benchmarkDataContainer, domainGenerator.getDomainData());
    }

    private ParsedDocumentIndex initializeIndex(IndexType indexType, String policyFolder) {
        SAPLInterpreter interpreter = new DefaultSAPLInterpreter();
        ParsedDocumentIndex documentIndex = getDocumentIndex(indexType);
        try {
            DirectoryStream stream = Files.newDirectoryStream(Paths.get(policyFolder), "*.sapl");

            try {
                Iterator var2 = stream.iterator();

                while (var2.hasNext()) {
                    Path filePath = (Path) var2.next();
                    LOGGER.trace("load: {}", filePath);
                    SAPL saplDocument = interpreter.parse(Files.newInputStream(filePath));
                    documentIndex.put(filePath.toString(), saplDocument);
                }
            } catch (Throwable var11) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                    }
                }

                throw var11;
            }

            if (stream != null) {
                stream.close();
            }

            documentIndex.setLiveMode();
        } catch (PolicyEvaluationException | IOException var12) {
            LOGGER.error("Error while initializing the document index.", var12);
        }

        return documentIndex;

    }

    private List<XlsRecord> run(PolicyGeneratorConfiguration config, String policyFolder,
                                BenchmarkDataContainer benchmarkDataContainer, DomainData domainData) {

        PolicyGenerator generator = new PolicyGenerator(config, domainData);

        List<XlsRecord> results = new LinkedList<>();

        LOGGER.info("running benchmark with config={}, runs={}", config.getName(), benchmarkDataContainer.getRuns());

        try {
            Map<String, JsonNode> variables = Collections.emptyMap();
            AnnotationFunctionContext functionContext = new AnnotationFunctionContext();
            AnnotationAttributeContext attributeContext = new AnnotationAttributeContext();

            DocumentsCombinator documentsCombinator = new DenyUnlessPermitCombinator();
            Objects.requireNonNull(documentsCombinator);


            LOGGER.info("init index");
            //create PRP
            long begin = System.nanoTime();
            ParsedDocumentIndex documentIndex = initializeIndex(benchmarkDataContainer
                    .getIndexType(), policyFolder);

//            FilesystemPolicyRetrievalPoint policyRetrievalPoint =
//                    createPRP(policyFolder, benchmarkDataContainer.getIndexType());
            double timePreparation = nanoToMs(System.nanoTime() - begin);

            //warm up
            try {
                for (int i = 0; i < 10; i++) {
                    documentIndex
                            .retrievePolicies(generator.createEmptySubscription(), functionContext, variables);
                }
            } catch (Exception ignored) {
                LOGGER.error("error during warm-up", ignored);
            }

            //generate AuthorizationSubscription
            List<AuthorizationSubscription> subscriptions = new LinkedList<>();
            for (int i = 0; i < benchmarkDataContainer.getRuns(); i++) {
                AuthorizationSubscription sub = generator.createRandomSubscription();
                subscriptions.add(sub);
                LOGGER.trace("generated sub: {}", sub);
            }

            for (int j = 0; j < benchmarkDataContainer.getRuns(); j++) {

                AuthorizationSubscription request = generator.getRandomElement(subscriptions);

                long start = System.nanoTime();
                PolicyRetrievalResult result =
                        documentIndex.retrievePolicies(request, functionContext, variables).block();
                long end = System.nanoTime();

                double timeRetrieve = nanoToMs(end - start);

                Objects.requireNonNull(result);

                AuthorizationDecision decision = documentsCombinator
                        .combineMatchingDocuments(result.getMatchingDocuments(), false,
                                request, attributeContext, functionContext, variables).blockFirst();

                Objects.requireNonNull(decision);

                results.add(new XlsRecord(j, config.getName(), timePreparation, timeRetrieve, request.toString(),
                        buildResponseStringForResult(result, decision)));

                LOGGER.debug("Total : {}ms", timeRetrieve);
            }

            LOGGER.info("destroy index");
            documentIndex.destroyIndex();

        } catch (Exception e) {
            LOGGER.error("Error running test", e);
        }

        return results;
    }

    private String buildResponseStringForResult(PolicyRetrievalResult policyRetrievalResult,
                                                AuthorizationDecision decision) {
        return String.format("PolicyRetrievalResult(decision=%s, matchingDocumentsCount=%d, errorsInTarget=%b)",
                decision.getDecision(), policyRetrievalResult.getMatchingDocuments().size(), policyRetrievalResult
                        .isErrorsInTarget());
    }


    private String generateRandomPolicies(PolicyGenerator generator, String path) throws IOException {

        String subfolder = generator.getConfig().getName().replaceAll("[^a-zA-Z0-9]", "");
        generator.generatePolicies(subfolder);

        final Path dir = Paths.get(path, subfolder);
        Files.createDirectories(dir);
        return subfolder;
    }


    private double nanoToMs(long nanoseconds) {
        return nanoseconds / MILLION;
    }
}
