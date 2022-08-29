package io.sapl.demo.axon.configuration;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ImpersonationUtil {

	public static Authentication impersonateSystemUser() {
		var securityContext = SecurityContextHolder.getContext();
		var originalAuthn   = securityContext.getAuthentication();
		var newAuthn        = new UsernamePasswordAuthenticationToken("SYSTEM", null,
				AuthorityUtils.commaSeparatedStringToAuthorityList("SYSTEM"));
		securityContext.setAuthentication(newAuthn);
		return originalAuthn;
	}

	public static void setUser(Authentication authn) {
		SecurityContextHolder.getContext().setAuthentication(authn);
	}
}
