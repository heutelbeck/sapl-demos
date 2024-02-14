package io.sapl.benchmark.util;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Text;
import reactor.core.publisher.Flux;

import java.time.Duration;

@PolicyInformationPoint(name = "echo", description = "PIP echoing the input value after 0,5 seconds")
public class EchoPIP {

    @SuppressWarnings("unused")
    @Attribute(name = "delayed")
    public static Flux<Val> delayed(@Text Val value) {
        return Flux.just(value).delayElements(Duration.ofMillis(500));
    }

}