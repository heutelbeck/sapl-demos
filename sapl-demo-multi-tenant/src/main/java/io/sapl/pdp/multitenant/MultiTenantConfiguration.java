package io.sapl.pdp.multitenant;

import java.io.File;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.interpreter.SAPLInterpreter;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.pdp.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.config.filesystem.FileSystemVariablesAndCombinatorSource;
import io.sapl.pdp.config.fixed.FixedFunctionsAndAttributesPDPConfigurationProvider;
import io.sapl.prp.GenericInMemoryIndexedPolicyRetrievalPoint;
import io.sapl.prp.filesystem.FileSystemPrpUpdateEventSource;
import io.sapl.prp.index.naive.NaiveImmutableParsedDocumentIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ComponentScan
@Configuration
@RequiredArgsConstructor
@Import(MultiTenantPolicyDecisionPointProperties.class)
public class MultiTenantConfiguration {

	private final MultiTenantPolicyDecisionPointProperties properties;

	private final SAPLInterpreter interpreter;
	private final FunctionContext functionContext;

	private final AttributeContext attributeContext;

	@Bean
	PolicyDecisionPoint multiTenantPolicyDecisionPoint(TenantIdExtractor tenantIdExtractor) {
		log.info("Multi Tenant settings: {}", properties);
		var file        = new File(properties.path);
		var directories = file.list((current, name) -> new File(current, name).isDirectory());
		var pdp         = new MultiTenantAwarePolicyDecisionPoint(tenantIdExtractor);
		for (var tenantId : directories) {
			log.info("Setting up PDP for tenant '{}'", tenantId);
			var path = properties.path + File.separatorChar + tenantId;
			pdp.loadTenantPolicyDecisionPoint(tenantId, policyDecisionPointForDirectory(path));
		}
		return pdp;
	}

	private PolicyDecisionPoint policyDecisionPointForDirectory(String path) {
		log.info("PDP for path: {}", path);
		var pdpUpdateEventSource  = new FileSystemPrpUpdateEventSource(path, interpreter);
		var seedIndex             = new NaiveImmutableParsedDocumentIndex();
		var policyRetrievalPoint  = new GenericInMemoryIndexedPolicyRetrievalPoint(seedIndex, pdpUpdateEventSource);
		var combinatorProvider    = new FileSystemVariablesAndCombinatorSource(path);
		var configurationProvider = new FixedFunctionsAndAttributesPDPConfigurationProvider(attributeContext,
				functionContext, combinatorProvider, List.of(), List.of());
		return new EmbeddedPolicyDecisionPoint(configurationProvider, policyRetrievalPoint);
	}
}
