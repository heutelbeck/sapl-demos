package io.sapl.demo.spring.controller;

import io.sapl.api.model.UndefinedValue;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
class HelloController {

    record HelloResponse(String message) {}

    private final PolicyDecisionPoint pdp;

    @GetMapping("/api/hello")
    Mono<HelloResponse> getHello() {
        var subscription = AuthorizationSubscription.of("anonymous", "read", "hello");
        return pdp.decideOnce(subscription).flatMap(decision -> {
            log.info("PDP decision: {}", decision.decision());
            if (decision.decision() == Decision.PERMIT
                    && decision.obligations().isEmpty()
                    && decision.resource() instanceof UndefinedValue) {
                return Mono.just(new HelloResponse("hello"));
            }
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied by policy"));
        });
    }

}
