package io.sapl.demo.spring.handler;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.RunnableConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class LogAccessHandler implements RunnableConstraintHandlerProvider {

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue obj)) {
            return false;
        }
        return obj.get("type") instanceof TextValue t && "logAccess".equals(t.value());
    }

    @Override
    public Signal getSignal() {
        return Signal.ON_DECISION;
    }

    @Override
    public Runnable getHandler(Value constraint) {
        var obj = (ObjectValue) constraint;
        var msgValue = obj.get("message");
        var message = msgValue instanceof TextValue t ? t.value() : "Access logged";
        return () -> log.info("[POLICY] {}", message);
    }

}
