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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/streaming")
class StreamingController {

    record StreamSignal(String type, String message) {}

    private final StreamingService streamingService;

    @GetMapping(value = "/heartbeat/till-denied", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatTillDenied() {
        return wrapWithDenySignal(streamingService.heartbeatTillDenied(),
                "ACCESS_DENIED", "Stream terminated by policy");
    }

    @GetMapping(value = "/heartbeat/drop-while-denied", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatDropWhileDenied() {
        return streamingService.heartbeatDropWhileDenied().map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/terminated-by-callback", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatTerminatedByCallback() {
        return wrapWithDenySignal(streamingService.heartbeatRecoverable(),
                "ACCESS_SUSPENDED", "Waiting for re-authorization");
    }

    @GetMapping(value = "/heartbeat/drop-with-callbacks", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatDropWithCallbacks() {
        return streamingService.heartbeatDropWhileDenied().map(StreamingController::toSse);
    }

    @GetMapping(value = "/heartbeat/recoverable", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> heartbeatRecoverable() {
        return streamingService.heartbeatRecoverable()
                .map(StreamingController::toSse)
                .onErrorResume(AccessDeniedException.class, e ->
                        Flux.concat(
                                Flux.just(toSse(new StreamSignal("ACCESS_SUSPENDED", "Waiting for re-authorization"))),
                                streamingService.heartbeatRecoverable().map(StreamingController::toSse)));
    }

    private static Flux<ServerSentEvent<Object>> wrapWithDenySignal(Flux<?> source, String type, String message) {
        return source.map(StreamingController::toSse)
                .onErrorResume(AccessDeniedException.class, e ->
                        Flux.just(toSse(new StreamSignal(type, message))));
    }

    private static ServerSentEvent<Object> toSse(Object data) {
        return ServerSentEvent.builder(data).build();
    }

}
