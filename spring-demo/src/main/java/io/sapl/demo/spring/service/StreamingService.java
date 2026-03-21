package io.sapl.demo.spring.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import io.sapl.spring.method.metadata.EnforceDropWhileDenied;
import io.sapl.spring.method.metadata.EnforceRecoverableIfDenied;
import io.sapl.spring.method.metadata.EnforceTillDenied;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StreamingService {

    public record HeartbeatEvent(int seq, String ts) {}

    @EnforceTillDenied(action = "'stream:heartbeat'", resource = "'heartbeat'")
    public Flux<HeartbeatEvent> heartbeatTillDenied() {
        return heartbeatSource();
    }

    @EnforceDropWhileDenied(action = "'stream:heartbeat'", resource = "'heartbeat'")
    public Flux<HeartbeatEvent> heartbeatDropWhileDenied() {
        return heartbeatSource();
    }

    @EnforceRecoverableIfDenied(action = "'stream:heartbeat'", resource = "'heartbeat'",
            signalAccessRecovery = true)
    public Flux<HeartbeatEvent> heartbeatRecoverable() {
        return heartbeatSource();
    }

    private static Flux<HeartbeatEvent> heartbeatSource() {
        var seq = new AtomicInteger(0);
        return Flux.interval(Duration.ofSeconds(2))
                .map(tick -> new HeartbeatEvent(seq.getAndIncrement(), Instant.now().toString()));
    }

}
