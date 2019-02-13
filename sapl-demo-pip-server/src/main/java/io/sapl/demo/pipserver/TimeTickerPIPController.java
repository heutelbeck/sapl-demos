package io.sapl.demo.pipserver;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("rest/time")
public class TimeTickerPIPController {

    @GetMapping(value = "ticker", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<JsonNode> getTimeTicker() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> JsonNodeFactory.instance.textNode(Instant.now().toString()));
    }

}
