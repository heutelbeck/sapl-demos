package io.sapl.demo.spring.handler;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Runner;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.DecisionSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class LogAccessHandler implements ConstraintHandlerProvider {

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        if (!(constraint instanceof ObjectValue obj)) {
            return List.of();
        }
        if (!(obj.get("type") instanceof TextValue(String type)) || !"logAccess".equals(type)) {
            return List.of();
        }
        if (!supportedSignals.contains(DecisionSignal.TYPE)) {
            return List.of();
        }
        var message = obj.get("message") instanceof TextValue(String text) ? text : "Access logged";
        Runner handler = () -> log.info("[POLICY] {}", message);
        return List.of(new ScopedConstraintHandler(handler, DecisionSignal.TYPE, 50));
    }
}
