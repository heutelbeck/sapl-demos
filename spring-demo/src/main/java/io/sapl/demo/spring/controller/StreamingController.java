package io.sapl.demo.spring.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.demo.spring.service.StreamingService;
import io.sapl.spring.pep.streaming.TransitionSignals;
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

    @GetMapping(value = "/heartbeat/silent-suspending", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatSilentSuspending() {
        return streamingService.heartbeatSilentSuspending().map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/observed-suspending", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatObservedSuspending() {
        Flux<Object> raw         = streamingService.heartbeatObservedSuspending().cast(Object.class);
        Flux<Object> withSuspend = TransitionSignals.onSuspend(raw, e -> {},
                () -> new StreamSignal("ACCESS_SUSPENDED", "Stream paused by policy"));
        return TransitionSignals.onGranted(withSuspend, e -> {},
                () -> new StreamSignal("ACCESS_GRANTED", "Access granted by policy"))
                .map(StreamingController::toSse);
    }

    private static ServerSentEvent<Object> toSse(Object data) {
        return ServerSentEvent.builder(data).build();
    }
}
