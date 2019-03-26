package org.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.AfterInvocationProviderManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.spring.SAPLProperties;
import io.sapl.spring.constraints.ConstraintHandlerService;
import io.sapl.spring.method.PolicyBasedEnforcementAttributeFactory;
import io.sapl.spring.method.PolicyEnforcementMethodSecurityMetadataSource;
import io.sapl.spring.method.post.PolicyBasedPostInvocationEnforcementAdvice;
import io.sapl.spring.method.post.PostInvocationEnforcementProvider;
import io.sapl.spring.method.pre.PolicyBasedPreInvocationEnforcementAdvice;
import io.sapl.spring.method.pre.PreInvocationEnforcementAdviceVoter;
import io.sapl.spring.runas.PolicyEngineRunAsManager;
import lombok.RequiredArgsConstructor;

@ComponentScan({ "io.sapl", "org.demo" })
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Value("${io.sapl.runaskey}")
	private String runAsKey;
	private final SAPLProperties pdpProperites;
	private final PolicyDecisionPoint pdp;
	private final ConstraintHandlerService constraintHandlers;
	private final ObjectMapper mapper;

	@Override
	protected AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
		PolicyBasedPreInvocationEnforcementAdvice policyAdvice = new PolicyBasedPreInvocationEnforcementAdvice(pdp,
				constraintHandlers, mapper);
		policyAdvice.setExpressionHandler(getExpressionHandler());
		decisionVoters.add(new PreInvocationEnforcementAdviceVoter(policyAdvice));
		decisionVoters.add(new RoleVoter());
		decisionVoters.add(new AuthenticatedVoter());
		return new AffirmativeBased(decisionVoters);
	}

	@Override
	protected AfterInvocationManager afterInvocationManager() {
		PolicyBasedPostInvocationEnforcementAdvice advice = new PolicyBasedPostInvocationEnforcementAdvice(pdp,
				constraintHandlers, mapper);
		advice.setExpressionHandler(getExpressionHandler());
		PostInvocationEnforcementProvider provider = new PostInvocationEnforcementProvider(advice);

		AfterInvocationProviderManager invocationProviderManager = (AfterInvocationProviderManager) super.afterInvocationManager();
		if (invocationProviderManager == null) {
			invocationProviderManager = new AfterInvocationProviderManager();
			List<AfterInvocationProvider> afterInvocationProviders = new ArrayList<>();
			afterInvocationProviders.add(provider);
			invocationProviderManager.setProviders(afterInvocationProviders);
		} else {
			List<AfterInvocationProvider> originalProviders = invocationProviderManager.getProviders();
			List<AfterInvocationProvider> afterInvocationProviders = new ArrayList<>();
			afterInvocationProviders.add(provider);
			afterInvocationProviders.addAll(originalProviders);
			invocationProviderManager.setProviders(afterInvocationProviders);
		}
		return invocationProviderManager;
	}

	@Override
	protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
		return new PolicyEnforcementMethodSecurityMetadataSource(
				new PolicyBasedEnforcementAttributeFactory(getExpressionHandler()));
	}

	@Override
	protected RunAsManager runAsManager() {
		PolicyEngineRunAsManager ram = new PolicyEngineRunAsManager(pdpProperites);
		ram.setKey(runAsKey);
		return ram;
	}

}
