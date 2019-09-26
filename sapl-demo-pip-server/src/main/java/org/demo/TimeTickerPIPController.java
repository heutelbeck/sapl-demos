package org.demo;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import reactor.core.publisher.Flux;

/**
 * REST controller providing an endpoint for the policy information point
 * emitting the current time with an interval of one second.
 * The endpoint can be connected using the client
 * {@code HttpPolicyInformationPoint} of the SAPL policy engine.
 */
@RestController
@RequestMapping("rest/time")
public class TimeTickerPIPController {

	/**
	 * Returns a flux emitting the current time once per second as a string
	 * in ISO-8601 format.
	 *
	 * @return a flux emitting the current time once per second as a string
	 * 	       in ISO-8601 format.
	 */
	@GetMapping(value = "ticker", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<JsonNode> getTimeTicker() {
		return Flux.interval(Duration.ofSeconds(1))
				.map(i -> JsonNodeFactory.instance.textNode(Instant.now().toString()));
	}

}
