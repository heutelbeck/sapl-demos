package io.sapl.pdp.multitenant;

import java.util.Optional;

import org.springframework.security.core.Authentication;

public interface TenantIdExtractor {
	Optional<String> tenantOf(Authentication authn); 
}
