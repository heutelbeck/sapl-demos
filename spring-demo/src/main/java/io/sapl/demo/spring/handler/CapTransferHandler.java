package io.sapl.demo.spring.handler;

import java.util.Arrays;
import java.util.function.Consumer;

import io.sapl.api.model.NumberValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.MethodInvocationConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class CapTransferHandler implements MethodInvocationConstraintHandlerProvider {

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue obj)) {
            return false;
        }
        return obj.get("type") instanceof TextValue t && "capTransferAmount".equals(t.value());
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(Value constraint) {
        var obj = (ObjectValue) constraint;
        var maxAmountValue = obj.get("maxAmount");
        var maxAmount = maxAmountValue instanceof NumberValue n ? n.value().doubleValue() : 0.0;
        return invocation -> {
            var args = invocation.getArguments();
            var newArgs = Arrays.copyOf(args, args.length);
            for (int i = 0; i < newArgs.length; i++) {
                if (newArgs[i] instanceof Double d && d > maxAmount) {
                    newArgs[i] = maxAmount;
                    log.info("Amount capped by policy: {} -> {}", d, maxAmount);
                    invocation.setArguments(newArgs);
                    return;
                }
            }
        };
    }

}
