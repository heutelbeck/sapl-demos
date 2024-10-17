package io.sapl.demo.books.domain;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.constraints.api.MethodInvocationConstraintHandlerProvider;

@Service
public class EnforceCategoryFilteringConstraintHandlerProvider implements MethodInvocationConstraintHandlerProvider {

    private static final String LIMIT_CATEGORIES = "limitCategoriesTo";

    @Override
    public boolean isResponsible(JsonNode constraint) {
        return constraint.has(LIMIT_CATEGORIES) && constraint.get(LIMIT_CATEGORIES).isArray();
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(JsonNode constraint) {
        return methodInvocation -> {

            final var constraintCategories = constraint.get(LIMIT_CATEGORIES);
            final var categories           = new ArrayList<Integer>();

            if (constraintCategories.size() == 0) {
                methodInvocation.setArguments(Optional.empty());
                return;
            }

            for (var category : constraintCategories)
                categories.add(category.asInt());

            methodInvocation.setArguments(Optional.of(categories));
        };
    }

}
