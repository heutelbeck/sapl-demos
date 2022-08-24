package io.sapl.demo.axon.command;

import java.time.Instant;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.stereotype.Component;

import io.sapl.demo.axon.command.MedicalRecordAPI.AuditingEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditingLog {

	@EventHandler
	public void on(AuditingEvent event, @Timestamp Instant timestamp) {
		log.info("###SaplAxon: An auditing event was logged at time = {} with message = {}", timestamp,
				event.getMessage());
	}

}
