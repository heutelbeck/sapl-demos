package io.sapl.demo.spring.controller;

import io.sapl.demo.spring.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static io.sapl.spring.method.reactive.RecoverableFluxes.recoverWith;

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
                        Flux.just(toSse(new StreamSignal(
                                "ACCESS_DENIED", "Stream terminated by policy"))));
    }

    @GetMapping(value = "/heartbeat/drop-while-denied", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatDropWhileDenied() {
        return streamingService.heartbeatDropWhileDenied().map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/terminated-by-callback", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatTerminatedByCallback() {
        return recoverWith(
                streamingService.heartbeatRecoverable().cast(Object.class),
                () -> new StreamSignal("ACCESS_SUSPENDED", "Waiting for re-authorization"))
                .map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/drop-with-callbacks", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatDropWithCallbacks() {
        return streamingService.heartbeatDropWhileDenied().map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/recoverable", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatRecoverable() {
        return recoverWith(
                streamingService.heartbeatRecoverable().cast(Object.class),
                e -> {}, () -> new StreamSignal("ACCESS_SUSPENDED", "Waiting for re-authorization"),
                r -> {}, () -> new StreamSignal("ACCESS_RESTORED", "Authorization restored"))
                .map(StreamingController::toSse);
    }

    private static ServerSentEvent<Object> toSse(Object data) {
        return ServerSentEvent.builder(data).build();
    }

}
