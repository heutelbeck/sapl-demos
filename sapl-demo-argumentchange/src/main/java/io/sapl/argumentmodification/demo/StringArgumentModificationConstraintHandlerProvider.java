package io.sapl.argumentmodification.demo;

import java.util.Arrays;
import java.util.function.Consumer;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.constraints.api.MethodInvocationConstraintHandlerProvider;

@Service
public class StringArgumentModificationConstraintHandlerProvider implements MethodInvocationConstraintHandlerProvider {

    private static final String SUFFIX = "suffix";

    @Override
    public boolean isResponsible(Value constraint) {
        return constraint instanceof ObjectValue ov && ov.containsKey(SUFFIX) && ov.get(SUFFIX) instanceof TextValue;
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(Value constraint) {
        return methodInvocation -> {
            Object[] originalArguments = methodInvocation.getArguments();
            Object[] newArguments      = Arrays.copyOf(originalArguments, originalArguments.length);

            for (int i = 0; i < newArguments.length; i++)
                if (newArguments[i] instanceof String && constraint instanceof ObjectValue ov && ov.containsKey(SUFFIX) && ov.get(SUFFIX) instanceof TextValue tv) {
                        newArguments[i] += tv.value();
                    }
            methodInvocation.setArguments(newArguments);
        };
    }
}
