package org.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import io.sapl.spring.PDPProperties;
import io.sapl.spring.runas.PolicyEngineRunAsManager;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Value("${io.sapl.runaskey}")
	private String runAsKey;
	private final PDPProperties pdpProperites;

	@Override
	public RunAsManager runAsManager() {
		PolicyEngineRunAsManager ram = new PolicyEngineRunAsManager(pdpProperites);
		ram.setKey(runAsKey);
		return ram;
	}

}
