package io.sapl.axondemo;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.axon.client.gateway.SaplQueryGateway;
import io.sapl.axondemo.domain.MedicalRecordAPI.CreateBloodCountCommand;
import io.sapl.axondemo.domain.MedicalRecordAPI.CreateMedicalRecord;
import io.sapl.axondemo.domain.MedicalRecordAPI.CreateMedicalRecordWithClinical;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchMedicalRecordSummaryQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchOxygenSaturationQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchPulseQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchSinglePulseQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.MedicalRecordSummary;
import io.sapl.axondemo.domain.MedicalRecordAPI.PulseRecord;
import io.sapl.axondemo.domain.MedicalRecordAPI.ReducedRecord;
import io.sapl.axondemo.domain.MedicalRecordAPI.UpdateBloodCount;
import io.sapl.axondemo.domain.MedicalRecordAPI.UpdateMedicalRecordCommand;
import io.sapl.axondemo.domain.MedicalRecordAPI.UpdateMedicalRecordCommandConstraintHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@Profile("client")
@RequiredArgsConstructor
public class DemoService implements ApplicationListener<ApplicationReadyEvent> {
    private final static String subscriptionScenarioID = "1";
    private final CommandGateway commandGateway;
    private final SaplQueryGateway queryGateway;
    private final ObjectMapper mapper;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> future = null;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        executor.schedule(() -> {
            commandGateway.send(new GenericCommandMessage<>(new CreateMedicalRecord(subscriptionScenarioID, "Mueller"))
                    .withMetaData(demoappUser()));
            createAndUpdateMedicalRecordsForDemo();
        }, 5, TimeUnit.SECONDS);
    }

    void createMedicalRecord(String id, String name) {
        commandGateway.sendAndWait(new CreateMedicalRecord(id, name));
    }
    
    void createMedicalRecordWithClinical(String id, String name, boolean hasClinicalRecordAvailable) {
        commandGateway.sendAndWait(new CreateMedicalRecordWithClinical(id, name, hasClinicalRecordAvailable));
    }


    void updateMedicalRecord(String id, double pulse, double oxygenSaturation) {
        commandGateway.sendAndWait(new UpdateMedicalRecordCommand(id, pulse, oxygenSaturation));
    }

    void updateMedicalRecordConstraintAnnotation(String id, double pulse, double oxygenSaturation) {
        commandGateway.sendAndWait(new UpdateMedicalRecordCommandConstraintHandler(id, pulse, oxygenSaturation));
    }

    void createBloodCountExamination(String id, int examinationId) {
        commandGateway.sendAndWait(new CreateBloodCountCommand(id, examinationId));
    }

    CompletableFuture<PulseRecord> getPulseById(String id) {
        return queryGateway.query(new FetchSinglePulseQuery(id), PulseRecord.class);
    }

    void updateBloodCount(String id, int examinationId, double hematicritValue) {
        commandGateway.sendAndWait(new UpdateBloodCount(id, examinationId, hematicritValue));
    }

    CompletableFuture<MedicalRecordSummary> getMedicalRecordById(String id) {
        var result = queryGateway.query(new FetchMedicalRecordSummaryQuery(id), MedicalRecordSummary.class);
        return result;
    }

    public Flux<MedicalRecordSummary> subscribeToMedicalRecord(String id) {
    	startUpdateCommand();
    	var result = queryGateway.subscriptionQuery(
                new FetchMedicalRecordSummaryQuery(id),
                ResponseTypes.instanceOf(MedicalRecordSummary.class),
                ResponseTypes.instanceOf(MedicalRecordSummary.class));
        result.initialResult().block();
        return result.updates().doOnError(exc -> log.info("Exception : {}", exc.toString())).doFinally(it-> result.close());
    }

    public Flux<ReducedRecord> subscribeToPulse(String id) {
    	startUpdateCommand();
    	var result = queryGateway.recoverableSubscriptionQuery(
                new FetchPulseQuery(id),
                ResponseTypes.instanceOf(ReducedRecord.class),
                ResponseTypes.instanceOf(ReducedRecord.class),
                exc -> log.info("Exception: {}", exc.toString()));
        result.initialResult().block();
        return result.updates().doFinally(it-> result.close());
    }

    public Flux<ReducedRecord> subscribeToOxygenSaturation(String id) {
    	startUpdateCommand();
    	var result = queryGateway.subscriptionQuery(
                new FetchOxygenSaturationQuery(id),
                ResponseTypes.instanceOf(ReducedRecord.class),
                ResponseTypes.instanceOf(ReducedRecord.class));
        result.initialResult().block();
        return result.updates().doFinally(it-> result.close());
    }

    public void stopUpdates() {
    	if (future != null) {
    		log.info("Stop sending update commands.");
    		future.cancel(true);
    		future = null;
    	}
    }
    
    private void startUpdateCommand() {
    	log.info("Start sending update commands. (delay=10sec, intervall=2sec)");
    	stopUpdates();
    	future = executor.scheduleAtFixedRate(() -> {
            var cmd = new UpdateMedicalRecordCommand(subscriptionScenarioID, getRandomNumber(40, 120), getRandomNumber(90, 100));
            var genericCmd = new GenericCommandMessage<>(cmd).withMetaData(demoappUser());
            commandGateway.send(genericCmd);
        }, 10, 2, TimeUnit.SECONDS);
    }

    private void createAndUpdateMedicalRecordsForDemo() {
        var createCommand = new CreateMedicalRecord("42", "Doe participates in trial");
        commandGateway.send(new GenericCommandMessage<>(createCommand).withMetaData(demoappUser()));
        createCommand = new CreateMedicalRecord("43", "Smith");
        commandGateway.send(new GenericCommandMessage<>(createCommand).withMetaData(demoappUser()));
        createCommand = new CreateMedicalRecord("40", "Jones");
        commandGateway.send(new GenericCommandMessage<>(createCommand).withMetaData(demoappUser()));
        var update43Command = new UpdateMedicalRecordCommand("43", getRandomNumber(40, 170), getRandomNumber(90, 100));
        commandGateway.send(new GenericCommandMessage<>(update43Command).withMetaData(demoappUser()));
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

	private Map<String, ObjectNode> demoappUser() {
		var subjectNode = mapper.createObjectNode();
		subjectNode.put("name", "demoapp");
		return Map.of("subject", subjectNode);
	}
}
