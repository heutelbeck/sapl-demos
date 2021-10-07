package org.demo.constraints;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.constraints.AbstractConstraintHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * This class demonstrates the implementation of a custom constraint handler for
 * the SAE spring-boot integration All spring components/beans implementing the
 * interface ContratintHandler are automatically discovered and registered by
 * the spring policy enforcement points.
 */
@Slf4j
@Service
public class LoggingConstraintHandler extends AbstractConstraintHandler {

	public LoggingConstraintHandler() {
		super(1); // Priority 1
	}

	/**
	 * Upon receiving a decision from the PDP containing a constraint, i.e. an
	 * advice or obligation, the PEP will check all registered ConstraintHandler
	 * beans and ask them if they are able to handle a given constraint as defined
	 * by the policy.
	 * 
	 * Generally, there is no specific scheme to constraints. Any JSON object may be
	 * an appropriate constraint. Its contents solely depends on the domain modeling
	 * decisions of the application and policy author.
	 * 
	 * So each ConstraintHandler requires knowledge about the domain. In this case
	 * it is assumed, that the constraint object contains a field 'type' to
	 * disambiguate different constraints from each other.
	 * 
	 * This ConstraintHandler in particular is for logging messages when access to a
	 * resource is granted. Thus, the canHandle method returns true, if the type
	 * equals 'message'.
	 * 
	 * The PEP must first check if the runtime environment has the ability to handle
	 * the constraint, as it must deny access to the resource if the constraint is
	 * an obligation that cannot be handled. In this case no other advice or
	 * obligations have to be followed.
	 * 
	 * It is a good practice to validate the overall constraint object given, as an
	 * invalid constraint cannot be handled and declining a constrAint at this stage
	 * leads to a clean behavior in case of obligations.
	 */
	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint != null && constraint.has("type")
				&& "logAccess".equals(constraint.findValue("type").asText());
	}

	/**
	 * The handle method actually acts on the given constraint and executes the
	 * implied behavior of the application.
	 */
	@Override
	public boolean preBlockingMethodInvocationOrOnAccessDenied(JsonNode constraint) {
		if (isResponsible(constraint) && constraint.has("message")) {
			log.info(constraint.findValue("message").asText());
			return true;
		}
		return false;
	}

}