package io.sapl.demo.axon.iface.rest;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.axon.queryhandling.SaplQueryGateway;
import io.sapl.demo.axon.command.MonitorType;
import io.sapl.demo.axon.query.vitals.api.VitalSignMeasurement;
import io.sapl.demo.axon.query.vitals.api.VitalSignsQueryAPI.FetchVitalSignOfPatient;
import io.sapl.demo.axon.query.vitals.api.VitalSignsQueryAPI.MonitorVitalSignOfPatient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VitalSignsController {

	private final SaplQueryGateway queryGateway;

	@GetMapping("/api/patients/{id}/vitals/{type}")
	Mono<ResponseEntity<VitalSignMeasurement>> fetchVitals(@PathVariable String id, @PathVariable MonitorType type) {
		return Mono
				.fromFuture(() -> queryGateway.query(new FetchVitalSignOfPatient(id, type),
						ResponseTypes.instanceOf(VitalSignMeasurement.class)))
				.map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("/api/patients/{id}/vitals/{type}/stream")
	Flux<ServerSentEvent<VitalSignMeasurement>> streamSingleVital(@PathVariable String id,
			@PathVariable MonitorType type) {
		var result = queryGateway.recoverableSubscriptionQuery(new MonitorVitalSignOfPatient(id, type),
				ResponseTypes.instanceOf(VitalSignMeasurement.class),
				ResponseTypes.instanceOf(VitalSignMeasurement.class), () -> log.info("AccessDenied"));
		return Flux.concat(result.initialResult().onErrorResume(AccessDeniedException.class, error -> {
			doOnAccessDenied(error, id, type);
			return Mono.empty();
		}), result.updates().onErrorContinue(AccessDeniedException.class,
				(error, reason) -> doOnAccessDenied(error, id, type)))
				.map(view -> ServerSentEvent.<VitalSignMeasurement>builder().data(view).build());
	}

	private void doOnAccessDenied(Throwable e, String id, MonitorType type) {
		log.warn("Access Denied on {} for patient {}. Data will resume when access is granted again. '{}'", type, id,
				e.getMessage());
	}
}
