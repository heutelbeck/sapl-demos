package io.sapl.demo.spring.handler;

import java.util.function.UnaryOperator;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.constraints.api.ErrorMappingConstraintHandlerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class EnrichErrorHandler implements ErrorMappingConstraintHandlerProvider {

    @Override
    public boolean isResponsible(Value constraint) {
        if (!(constraint instanceof ObjectValue obj)) {
            return false;
        }
        return obj.get("type") instanceof TextValue t && "enrichError".equals(t.value());
    }

    @Override
    public UnaryOperator<Throwable> getHandler(Value constraint) {
        var obj = (ObjectValue) constraint;
        var urlValue = obj.get("supportUrl");
        var supportUrl = urlValue instanceof TextValue t ? t.value() : "https://support.example.com";
        return error -> {
            log.info("[ERROR-ENRICH] Enriching error with support URL: {}", supportUrl);
            var enriched = new RuntimeException(error.getMessage() + " | Support: " + supportUrl);
            enriched.initCause(error);
            return enriched;
        };
    }

}
