package io.sapl.argumentmodification.demo;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.constraints.api.MethodInvocationConstraintHandlerProvider;

@Service
public class StringArgumentModificationConstraintHandlerProvider
		implements MethodInvocationConstraintHandlerProvider {

	private static final String SUFFIX = "suffix";

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint.has(SUFFIX) && constraint.get(SUFFIX).isTextual();
	}

	@Override
	public Consumer<ReflectiveMethodInvocation> getHandler(JsonNode constraint) {
		return methodInvocation -> {
			Object[] originalArguments = methodInvocation.getArguments();
			Object[] newArguments      = Arrays.copyOf(originalArguments, originalArguments.length);

			for (int i = 0; i < newArguments.length; i++)
				if (newArguments[i] instanceof String)
					newArguments[i] += constraint.get(SUFFIX).textValue();

			methodInvocation.setArguments(newArguments);
		};
	}
}
