package org.demo.constraints;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.pep.ConstraintHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * This class demonstrates the implementation of a custom constraint handler for
 * the SAE spring-boot integration All spring components/beans implementing the
 * interface ContratintHandler are automatically discovered and registered by
 * the spring policy enforcement points.
 */
@Slf4j
@Component
public class EmailConstraintHandler implements ConstraintHandler {

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
	 * This ConstraintHandler in particular is for sending email messages when
	 * access to a resource is granted. Thus, the canHandle method returns true, if
	 * the type equals 'sendEmail'.
	 * 
	 * The PEP must first check if the runtime environment has the ability to handle
	 * the constraint, as it must deny access to the resource if the constraint is
	 * an obligation that cannot be handled. In this case no other advices or
	 * obligations have to be followed.
	 * 
	 * It is a good practice to validate the overall constraint object given, as an
	 * invalid constraint cannot be handled and declining a constrAint at this stage
	 * leads to a clean behavior in case of obligations. This dummy implementation
	 * does not check for a valid email address, which should be done.
	 */
	@Override
	public boolean canHandle(JsonNode constraint) {
		return constraint != null && constraint.has("type") && "sendEmail".equals(constraint.findValue("type").asText())
				&& constraint.has("recipient") && constraint.has("subject") && constraint.has("message");
	}

	/**
	 * The handle method actually acts on the given constraint and executes the
	 * implied behavior of the application.
	 */
	@Override
	public boolean handle(JsonNode constraint) {
		if (canHandle(constraint)) {
			sendEmail(constraint.findValue("recipient").asText(), constraint.findValue("subject").asText(),
					constraint.findValue("message").asText());
			return true;
		}
		return false;
	}

	/**
	 * This methods sends an email. For the demo purposes this is only printing a
	 * log message. For a real application use a matching mail sender
	 * implementation.
	 * 
	 * @param recipient the recipient email address
	 * @param subject   the subject of the mail
	 * @param message   the message
	 */
	private static void sendEmail(String recipient, String subject, String message) {
		log.info("An E-Mail has been sent to {} with the subject '{}' and the message '{}'.", recipient, subject,
				message);
	}

}