package io.sapl.demo.spring.handler;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Consumer;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.ErrorSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class NotifyOnErrorHandler implements ConstraintHandlerProvider {

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"notifyOnError".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(ErrorSignal.TYPE)) {
            return List.of();
        }
        Consumer<Throwable> handler = error -> log
                .warn("[ERROR-NOTIFY] Error during policy-protected operation: {}", error.getMessage());
        return List.of(new ScopedConstraintHandler(handler, ErrorSignal.TYPE, 50));
    }
}
