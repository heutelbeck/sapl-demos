package io.sapl.demo.spring.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.demo.spring.service.StreamingService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/streaming")
class StreamingController {

    record StreamSignal(String type, String message) {}

    private final StreamingService streamingService;

    @GetMapping(value = "/heartbeat/till-denied", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatTillDenied() {
        return streamingService.heartbeatTillDenied()
                .map(StreamingController::toSse)
                .onErrorResume(AccessDeniedException.class, e ->
                        Flux.just(toSse(new StreamSignal("ACCESS_DENIED", "Stream terminated by policy"))));
    }

    @GetMapping(value = "/heartbeat/drop-while-denied", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatDropWhileDenied() {
        return streamingService.heartbeatDropWhileDenied().map(StreamingController::toSse);
    }

    // TODO recover behaviour: the 4.0 io.sapl.spring.method.reactive.RecoverableFluxes
    // helper used to inject suspend/resume signals into the stream. Once the
    // streaming PEPs (@EnforceTillDenied, @EnforceDropWhileDenied,
    // @EnforceRecoverableIfDenied) ship as real enforcement (they are
    // scaffolds in this build), revisit and rebuild the
    // "deny -> ACCESS_SUSPENDED -> resume -> ACCESS_RESTORED" lifecycle
    // around the new lifecycle signals (SubscriptionSignal, CancelSignal,
    // CompleteSignal, TerminationSignal, AfterTerminationSignal). For now
    // both endpoints below pass the protected stream through unchanged.
    @GetMapping(value = "/heartbeat/terminated-by-callback", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatTerminatedByCallback() {
        return streamingService.heartbeatRecoverable().cast(Object.class).map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/drop-with-callbacks", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatDropWithCallbacks() {
        return streamingService.heartbeatDropWhileDenied().map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/recoverable", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatRecoverable() {
        return streamingService.heartbeatRecoverable().cast(Object.class).map(StreamingController::toSse);
    }

    private static ServerSentEvent<Object> toSse(Object data) {
        return ServerSentEvent.builder(data).build();
    }
}
