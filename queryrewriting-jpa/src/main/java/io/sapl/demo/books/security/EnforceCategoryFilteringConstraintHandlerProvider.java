package io.sapl.demo.books.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import io.sapl.api.model.ArrayValue;
import io.sapl.api.model.NumberValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.InputSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.val;

/**
 * Mutates the {@code filter} argument of the protected repository method
 * based on a SAPL obligation containing a {@code limitCategoriesTo} array.
 * The repository's JPA query then naturally restricts the result set to the
 * categories the policy allowed for the subject.
 * </p>
 * Migrated from the legacy {@code MethodInvocationConstraintHandlerProvider}
 * to the unified {@link ConstraintHandlerProvider} interface returning a
 * {@link Mapper}{@code <MethodInvocation>} attached to {@link InputSignal}.
 */
@Service
public class EnforceCategoryFilteringConstraintHandlerProvider implements ConstraintHandlerProvider {

    private static final String LIMIT_CATEGORIES = "limitCategoriesTo";
    private static final int    DEFAULT_PRIORITY = 50;

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get(LIMIT_CATEGORIES) instanceof ArrayValue constraintCategories)) {
            return List.of();
        }
        if (!supportedSignals.contains(InputSignal.SIGNAL_TYPE)) {
            return List.of();
        }
        Mapper<MethodInvocation> mapper = invocation -> rewriteFilterArgument(invocation, constraintCategories);
        return List.of(new ScopedConstraintHandler(mapper, InputSignal.SIGNAL_TYPE, DEFAULT_PRIORITY));
    }

    private static MethodInvocation rewriteFilterArgument(MethodInvocation invocation,
            ArrayValue constraintCategories) {
        if (!(invocation instanceof ReflectiveMethodInvocation reflective)) {
            return invocation;
        }
        if (constraintCategories.isEmpty()) {
            reflective.setArguments(Optional.empty());
            return invocation;
        }
        val categories = new ArrayList<Integer>();
        for (val category : constraintCategories) {
            if (category instanceof NumberValue(java.math.BigDecimal value)) {
                categories.add(value.intValue());
            }
        }
        reflective.setArguments(Optional.of(categories));
        return invocation;
    }
}
