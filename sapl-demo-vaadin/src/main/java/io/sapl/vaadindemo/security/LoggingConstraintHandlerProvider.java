package io.sapl.vaadindemo.security;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.spring.constraints.api.RunnableConstraintHandlerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This Constraint Handler Provider can be used to log messages based on SAPL Obligations.
 *
 * This provider manages constrains with id "log", here an example:
 * ...
 * obligation
 *     {
 *         "type": "log",
 *         "message"  : "test message"
 *     }
 * ...
 *
 */
@Service
public class LoggingConstraintHandlerProvider implements RunnableConstraintHandlerProvider {

    Logger logger = LoggerFactory.getLogger(LoggingConstraintHandlerProvider.class);

    @Override
    public boolean isResponsible(JsonNode constraint) {
        return constraint != null && constraint.has("type")
                && "log".equals(constraint.findValue("type").asText());
    }

    @Override
    public Signal getSignal() {
        return Signal.ON_COMPLETE;
    }

    /**
     * The handle method actually acts on the given constraint and logs the policy-defined message to console.
     */
    @Override
    public Runnable getHandler(JsonNode constraint) {
        return () -> {
            if (constraint != null && constraint.has("message")) {
                var message = constraint.findValue("message").asText();
                this.logger.info(message);
            }
        };
    }
}
