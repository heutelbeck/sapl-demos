package io.sapl.demo.webflux.argumentmodification;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.MethodInvocationConstraintHandlerProvider;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Consumer;

@Service
public class StringArgumentModificationConstraintHandlerProvider implements MethodInvocationConstraintHandlerProvider {

    private static final String SUFFIX = "suffix";

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue objectValue)) {
            return false;
        }
        return objectValue.containsKey(SUFFIX) && objectValue.get(SUFFIX) instanceof TextValue;
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(Value constraint) {
        var suffix = ((TextValue) ((ObjectValue) constraint).get(SUFFIX)).value();
        return methodInvocation -> {
            Object[] originalArguments = methodInvocation.getArguments();
            Object[] newArguments      = Arrays.copyOf(originalArguments, originalArguments.length);

            for (int i = 0; i < newArguments.length; i++)
                if (newArguments[i] instanceof String str)
                    newArguments[i] = str + suffix;

            methodInvocation.setArguments(newArguments);
        };
    }
}
