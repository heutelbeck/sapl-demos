package org.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import io.sapl.spring.SAPLProperties;
import io.sapl.spring.method.ExpressionBasedPolicyEnforcementAttributeFactory;
import io.sapl.spring.method.PolicyBasedPreInvocationEnforcementAdvice;
import io.sapl.spring.method.PolicyEnforcementMethodSecurityMetadataSource;
import io.sapl.spring.method.PreInvocationEnforcementAdviceVoter;
import io.sapl.spring.runas.PolicyEngineRunAsManager;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Value("${io.sapl.runaskey}")
	private String runAsKey;
	private final SAPLProperties pdpProperites;

	@Override
	protected AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
		PolicyBasedPreInvocationEnforcementAdvice policyAdvice = new PolicyBasedPreInvocationEnforcementAdvice();
		policyAdvice.setExpressionHandler(getExpressionHandler());
		decisionVoters.add(new PreInvocationEnforcementAdviceVoter(policyAdvice));
		decisionVoters.add(new RoleVoter());
		decisionVoters.add(new AuthenticatedVoter());
		return new AffirmativeBased(decisionVoters);
	}

	@Override
	protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
		return new PolicyEnforcementMethodSecurityMetadataSource(
				new ExpressionBasedPolicyEnforcementAttributeFactory(getExpressionHandler()));
	}

	@Override
	protected RunAsManager runAsManager() {
		PolicyEngineRunAsManager ram = new PolicyEngineRunAsManager(pdpProperites);
		ram.setKey(runAsKey);
		return ram;
	}

}
