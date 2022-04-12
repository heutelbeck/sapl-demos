package io.sapl.axondemo.domain;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.EntityId;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.axon.annotations.ConstraintHandler;
import io.sapl.spring.method.metadata.PreEnforce;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Profile("backend")
@NoArgsConstructor
public class BloodCount {
    @EntityId
    private int examinationId;
    private double hematocritValue;
    private boolean resultAvailable = false;

    public BloodCount(int examinationId) {
		this.examinationId = examinationId;
	}

    @PreEnforce
    @CommandHandler
    public void handle(MedicalRecordAPI.UpdateBloodCount command) {
        apply(new MedicalRecordAPI.BloodCountUpdatedEvent(command.getHematocritValue()));
    }
    
    @ConstraintHandler("#constraint.get('log event').asText().equals('blood count event')")
    public void logClinicalRecordOnlyIfRequired(MedicalRecordAPI.UpdateBloodCount command, JsonNode constraint, MetaData metaData) throws  Exception {
    	apply(new MedicalRecordAPI.BloodCountLogEvent(examinationId, command.getHematocritValue()));
    }

    @EventSourcingHandler
    public void handle(MedicalRecordAPI.BloodCountUpdatedEvent event) {
        hematocritValue = event.getHematocritValue();
        resultAvailable = true;
    }
}
