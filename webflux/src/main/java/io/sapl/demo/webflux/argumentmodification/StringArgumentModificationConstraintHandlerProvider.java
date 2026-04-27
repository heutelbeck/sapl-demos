package io.sapl.demo.webflux.argumentmodification;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.InputSignal;
import io.sapl.spring.pep.constraints.SignalType;

/**
 * 4.1 InputSignal mapper. Appends a suffix from the obligation to every
 * String argument of the protected method invocation.
 */
@Service
public class StringArgumentModificationConstraintHandlerProvider implements ConstraintHandlerProvider {

    private static final String SUFFIX = "suffix";

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get(SUFFIX) instanceof TextValue(String suffix))) {
            return List.of();
        }
        if (!supportedSignals.contains(InputSignal.TYPE)) {
            return List.of();
        }
        Mapper<MethodInvocation> mapper = invocation -> {
            Object[] originalArguments = invocation.getArguments();
            Object[] newArguments      = Arrays.copyOf(originalArguments, originalArguments.length);
            for (int i = 0; i < newArguments.length; i++) {
                if (newArguments[i] instanceof String str) {
                    newArguments[i] = str + suffix;
                }
            }
            if (invocation instanceof ReflectiveMethodInvocation reflective) {
                reflective.setArguments(newArguments);
            }
            return invocation;
        };
        return List.of(new ScopedConstraintHandler(mapper, InputSignal.TYPE, 50));
    }
}
