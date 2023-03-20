package io.sapl.demo.axon.command.patient;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatientConfiguration {
	@Bean
	SnapshotTriggerDefinition patientSnapshotTrigger(Snapshotter snapshotter) {
		return new EventCountSnapshotTriggerDefinition(snapshotter, 2);
	}
}
