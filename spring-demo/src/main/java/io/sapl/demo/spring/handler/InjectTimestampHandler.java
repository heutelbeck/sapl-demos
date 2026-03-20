package io.sapl.demo.spring.handler;

import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.MethodInvocationConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class InjectTimestampHandler implements MethodInvocationConstraintHandlerProvider {

    private static final String TARGET_PARAMETER = "policyTimestamp";

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue obj)) {
            return false;
        }
        return obj.get("type") instanceof TextValue t && "injectTimestamp".equals(t.value());
    }

    @Override
    public Consumer<ReflectiveMethodInvocation> getHandler(Value constraint) {
        return invocation -> {
            var timestamp = Instant.now().toString();
            var params = invocation.getMethod().getParameters();
            var args = invocation.getArguments();
            var newArgs = Arrays.copyOf(args, args.length);
            for (int i = 0; i < params.length; i++) {
                if (TARGET_PARAMETER.equals(params[i].getName())) {
                    newArgs[i] = timestamp;
                    break;
                }
            }
            invocation.setArguments(newArgs);
            log.info("[METHOD] Injected policy timestamp: {}", timestamp);
        };
    }

}
