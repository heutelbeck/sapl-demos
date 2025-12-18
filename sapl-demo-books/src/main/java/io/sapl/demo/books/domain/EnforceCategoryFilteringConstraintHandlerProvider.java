package io.sapl.demo.books.domain;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import io.sapl.api.model.ArrayValue;
import io.sapl.api.model.NumberValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.Value;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.constraints.api.MethodInvocationConstraintHandlerProvider;

@Service
public class EnforceCategoryFilteringConstraintHandlerProvider implements MethodInvocationConstraintHandlerProvider {

    private static final String LIMIT_CATEGORIES = "limitCategoriesTo";

    @Override
    public boolean isResponsible(Value constraint) {
        return constraint instanceof ObjectValue ov && ov.containsKey(LIMIT_CATEGORIES) && ov.get(LIMIT_CATEGORIES) instanceof ArrayValue;
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(Value constraint) {
        return methodInvocation -> {
            if (constraint instanceof ObjectValue ov && ov.containsKey(LIMIT_CATEGORIES) && ov.get(LIMIT_CATEGORIES) instanceof ArrayValue constraintCategories) {
                final var categories = new ArrayList<Integer>();

                if (constraintCategories.isEmpty()) {
                    methodInvocation.setArguments(Optional.empty());
                    return;
                }

                for (var category : constraintCategories) {
                    if (category instanceof NumberValue categoryNumber) {
                        categories.add(categoryNumber.value().intValue());
                    }
                }
                methodInvocation.setArguments(Optional.of(categories));
            }
        };
    }

}
