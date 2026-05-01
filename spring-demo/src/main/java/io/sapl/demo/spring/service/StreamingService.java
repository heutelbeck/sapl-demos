package io.sapl.demo.spring.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import io.sapl.spring.method.metadata.StreamEnforce;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StreamingService {

    public record HeartbeatEvent(int seq, String ts) {}

    /**
     * Default {@code @StreamEnforce}: terminates on the first non-PERMIT
     * decision. Companion policy uses {@code deny} during the closed
     * window (action {@code stream:heartbeat:terminate}) so the visible
     * behaviour is "stream until denied terminates."
     */
    @StreamEnforce(action = "'stream:heartbeat:terminate'", resource = "'heartbeat'")
    public Flux<HeartbeatEvent> heartbeatTillDenied() {
        return heartbeatSource();
    }

    /**
     * Default {@code @StreamEnforce} against a policy that uses
     * {@code suspend} during the closed window (action
     * {@code stream:heartbeat:suspend}). The subscription survives the
     * suspension; items are dropped silently; the next PERMIT resumes
     * the flow. Subscriber sees no boundary signals.
     */
    @StreamEnforce(action = "'stream:heartbeat:suspend'", resource = "'heartbeat'")
    public Flux<HeartbeatEvent> heartbeatDropWhileDenied() {
        return heartbeatSource();
    }

    /**
     * {@code @StreamEnforce(signalTransitions = true)} against the same
     * suspend-using policy as {@link #heartbeatDropWhileDenied()}. The
     * subscription survives; items are dropped silently while suspended;
     * boundary crossings surface as non-terminal exceptions on the error
     * channel. Subscribers consume them via {@code onErrorContinue} or
     * {@code RecoverableFluxes}.
     */
    @StreamEnforce(action = "'stream:heartbeat:suspend'", resource = "'heartbeat'", signalTransitions = true)
    public Flux<HeartbeatEvent> heartbeatRecoverable() {
        return heartbeatSource();
    }

    private static Flux<HeartbeatEvent> heartbeatSource() {
        var seq = new AtomicInteger(0);
        return Flux.interval(Duration.ofSeconds(2))
                .map(tick -> new HeartbeatEvent(seq.getAndIncrement(), Instant.now().toString()));
    }

}
