package io.sapl.demo.spring.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Component;

import io.sapl.api.model.NumberValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.InputSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class CapTransferHandler implements ConstraintHandlerProvider {

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"capTransferAmount".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(InputSignal.SIGNAL_TYPE)) {
            return List.of();
        }
        var maxAmount = obj.get("maxAmount") instanceof NumberValue(java.math.BigDecimal n) ? n.doubleValue() : 0.0;
        Mapper<MethodInvocation> mapper = invocation -> {
            var args    = invocation.getArguments();
            var newArgs = Arrays.copyOf(args, args.length);
            for (int i = 0; i < newArgs.length; i++) {
                if (newArgs[i] instanceof Double d && d > maxAmount) {
                    newArgs[i] = maxAmount;
                    log.info("Amount capped by policy: {} -> {}", d, maxAmount);
                    if (invocation instanceof ReflectiveMethodInvocation reflective) {
                        reflective.setArguments(newArgs);
                    }
                    return invocation;
                }
            }
            return invocation;
        };
        return List.of(new ScopedConstraintHandler(mapper, InputSignal.SIGNAL_TYPE, 50));
    }
}
