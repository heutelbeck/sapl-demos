package org.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private final PolicyDecisionPoint pdp;

    @GetMapping("/hello")
    public String handle() {
        return "Hello WebFlux";
    }

    @GetMapping(path = "/authSub", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AuthorizationDecision> getResult() {
        LOGGER.info("getResult called");

        String subjectJsonString = "{}";
        String actionJsonString = "getPatients";

        ObjectMapper mapper = new ObjectMapper();

        AuthorizationSubscription authorizationSubscription =
                buildAuthorizationSubscription("marc", "getPatients", "patients");

        Flux<AuthorizationDecision> decisionFlux = pdp.decide(authorizationSubscription);

        return decisionFlux;
    }

    private static AuthorizationSubscription buildAuthorizationSubscription(Object subject, Object action,
                                                                            Object resource) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return new AuthorizationSubscription(mapper.valueToTree(subject), mapper.valueToTree(action),
                mapper.valueToTree(resource), null);
    }

}
