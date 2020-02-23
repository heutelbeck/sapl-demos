package org.demo.security;

import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.demo.domain.PrinterUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.server.ServletHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static PrinterUser getUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Object principal = context.getAuthentication().getPrincipal();
		if (principal instanceof PrinterUser) {
			PrinterUser printerUser = (PrinterUser) context.getAuthentication().getPrincipal();
			return printerUser;
		}
		return null;
	}

	/**
	 * Tests if the request is an internal framework request. The test consists of checking if the request parameter is
	 * present and if its value is consistent with any of the request types know.
	 *
	 * @param request {@link HttpServletRequest}
	 * @return true if is an internal framework request. False otherwise.
	 */
	static boolean isFrameworkInternalRequest(HttpServletRequest request) {
		final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
		return parameterValue != null
				&& Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
	}

	/**
	 * Tests if some user is authenticated. As Spring Security always will create an
	 * {@link AnonymousAuthenticationToken} we have to ignore those tokens explicitly.
	 */
	static boolean isUserLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}
}