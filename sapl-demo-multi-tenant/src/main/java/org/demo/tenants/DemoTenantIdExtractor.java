package org.demo.tenants;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.sapl.pdp.multitenant.TenantIdExtractor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DemoTenantIdExtractor implements TenantIdExtractor {

	@Override
	public Optional<String> tenantOf(Authentication authn) {
		if (authn == null)
			return Optional.empty();

		var principal = authn.getPrincipal();
		if (!(principal instanceof TenantAwareUserDetails))
			return Optional.empty();

		return Optional.of(((TenantAwareUserDetails) principal).getTenantId());
	}

}
