package io.sapl.demo.spring.handler;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Component;

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
class InjectTimestampHandler implements ConstraintHandlerProvider {

    private static final String TARGET_PARAMETER = "policyTimestamp";

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"injectTimestamp".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(InputSignal.TYPE)) {
            return List.of();
        }
        Mapper<MethodInvocation> mapper = invocation -> {
            var timestamp = Instant.now().toString();
            var params    = invocation.getMethod().getParameters();
            var args      = invocation.getArguments();
            var newArgs   = Arrays.copyOf(args, args.length);
            for (int i = 0; i < params.length; i++) {
                if (TARGET_PARAMETER.equals(params[i].getName())) {
                    newArgs[i] = timestamp;
                    break;
                }
            }
            if (invocation instanceof ReflectiveMethodInvocation reflective) {
                reflective.setArguments(newArgs);
            }
            log.info("[METHOD] Injected policy timestamp: {}", timestamp);
            return invocation;
        };
        return List.of(new ScopedConstraintHandler(mapper, InputSignal.TYPE, 50));
    }
}
