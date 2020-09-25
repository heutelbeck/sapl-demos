package org.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class,
        ReactiveUserDetailsServiceAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class TestApplication implements CommandLineRunner {

    private static final Object NULLNODE = JsonNodeFactory.instance.nullNode();
    private final PolicyDecisionPoint pdp;

    public static void main(String[] args) {
        new SpringApplicationBuilder(TestApplication.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        AuthorizationSubscription authorizationSubscription = createEmptySubscription();

        Flux<AuthorizationDecision> decisionFlux = pdp.decide(authorizationSubscription);
        AuthorizationDecision authorizationDecision = decisionFlux.blockFirst();

        LOGGER.info("decision= {}", authorizationDecision.getDecision());

    }

    public AuthorizationSubscription createEmptySubscription() {
        return buildAuthorizationSubscription(NULLNODE, NULLNODE, NULLNODE);
    }

    private AuthorizationSubscription buildAuthorizationSubscription(Object subject, Object action,
                                                                     Object resource) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return new AuthorizationSubscription(mapper.valueToTree(subject), mapper.valueToTree(action),
                mapper.valueToTree(resource), null);
    }

}
