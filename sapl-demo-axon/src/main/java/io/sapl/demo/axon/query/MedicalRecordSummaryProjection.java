package io.sapl.demo.axon.query;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.AllowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.sapl.demo.axon.command.MedicalRecordAPI.MedicalRecordCreatedEvent;
import io.sapl.demo.axon.command.MedicalRecordAPI.MedicalRecordUpdatedEvent;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.CountChangedUpdate;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.CountMedicalRecordSummariesQuery;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.CountMedicalRecordSummariesResponse;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.FetchMedicalRecordSummariesQuery;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.FetchMedicalRecordSummaryQuery;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.FetchOxygenSaturationQuery;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.FetchPulseQuery;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.FetchSinglePulseQuery;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.MedicalRecordSummary;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.PulseRecord;
import io.sapl.demo.axon.query.MedicalRecordSummaryAPI.ReducedRecord;
import io.sapl.spring.method.metadata.EnforceDropWhileDenied;
import io.sapl.spring.method.metadata.EnforceRecoverableIfDenied;
import io.sapl.spring.method.metadata.EnforceTillDenied;
import io.sapl.spring.method.metadata.PostEnforce;

@Service
@AllowReplay
@Profile("backend")
@ProcessingGroup("medicalRecordProjection")
public class MedicalRecordSummaryProjection {

	private final SortedMap<String, MedicalRecordSummary> medicalSummaryReadModel;
	private final QueryUpdateEmitter                      queryUpdateEmitter;

	public MedicalRecordSummaryProjection(QueryUpdateEmitter queryUpdateEmitter) {
		this.medicalSummaryReadModel = new ConcurrentSkipListMap<>();
		this.queryUpdateEmitter      = queryUpdateEmitter;
	}

	@EventHandler
	public void on(MedicalRecordCreatedEvent event) {

		medicalSummaryReadModel.put(event.getId(), new MedicalRecordSummary(event.getId(), event.getName(), -1, -1));
		queryUpdateEmitter.emit(CountMedicalRecordSummariesQuery.class, query -> true, new CountChangedUpdate());
	}

	@EventHandler
	public void on(MedicalRecordUpdatedEvent event) {

		MedicalRecordSummary summary = medicalSummaryReadModel.get(event.getId());
		summary.setPulse(event.getPulse());
		summary.setOxygenSaturation(event.getOxygenSaturation());

		queryUpdateEmitter.emit(FetchMedicalRecordSummariesQuery.class, query -> true, summary);
		queryUpdateEmitter.emit(FetchMedicalRecordSummaryQuery.class, query -> query.getId().equals(summary.getId()),
				summary);
		queryUpdateEmitter.emit(FetchPulseQuery.class, query -> query.getId().equals(summary.getId()),
				new ReducedRecord(summary.getPatientName(), summary.getPulse()));
		queryUpdateEmitter.emit(FetchOxygenSaturationQuery.class, query -> query.getId().equals(summary.getId()),
				new ReducedRecord(summary.getPatientName(), summary.getOxygenSaturation()));
	}

	@QueryHandler
	public List<MedicalRecordSummary> handle(FetchMedicalRecordSummariesQuery query) {
		MedicalRecordSummary[] medicalRecordSummaryArray = medicalSummaryReadModel.values()
				.toArray(MedicalRecordSummary[]::new);
		return Arrays.stream(medicalRecordSummaryArray, query.getOffset(), medicalRecordSummaryArray.length)
				.limit(query.getLimit()).collect(Collectors.toList());
	}

	@QueryHandler
	public CountMedicalRecordSummariesResponse handle(CountMedicalRecordSummariesQuery query) {
		return new CountMedicalRecordSummariesResponse(medicalSummaryReadModel.size(), Instant.now().toEpochMilli());
	}

	@QueryHandler
	@PostEnforce
	@EnforceTillDenied
	public MedicalRecordSummary handle(FetchMedicalRecordSummaryQuery query) {
		return medicalSummaryReadModel.get(query.getId());
	}

	@QueryHandler
	@PostEnforce
	public PulseRecord handle(FetchSinglePulseQuery query) {
		var res = medicalSummaryReadModel.get(query.getId());
		return new PulseRecord(res.getPulse());
	}

	@QueryHandler
	@EnforceRecoverableIfDenied
	public ReducedRecord handle(FetchPulseQuery query) {
		var res = medicalSummaryReadModel.get(query.getId());
		return new ReducedRecord(res.getPatientName(), res.getPulse());
	}

	@QueryHandler
	@EnforceDropWhileDenied
	public ReducedRecord handle(FetchOxygenSaturationQuery query) {
		var res = medicalSummaryReadModel.get(query.getId());
		return new ReducedRecord(res.getPatientName(), res.getOxygenSaturation());
	}

}
