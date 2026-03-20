package io.sapl.demo.spring.handler;

import java.util.function.Consumer;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.ErrorHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class NotifyOnErrorHandler implements ErrorHandlerProvider {

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue obj)) {
            return false;
        }
        return obj.get("type") instanceof TextValue t && "notifyOnError".equals(t.value());
    }

    @Override
    public Consumer<Throwable> getHandler(Value constraint) {
        return error -> log.warn("[ERROR-NOTIFY] Error during policy-protected operation: {}",
                error.getMessage());
    }

}
