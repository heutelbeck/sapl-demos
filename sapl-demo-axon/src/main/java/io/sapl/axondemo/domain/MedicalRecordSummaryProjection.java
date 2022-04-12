package io.sapl.axondemo.domain;

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

import io.sapl.axondemo.domain.MedicalRecordAPI.CountChangedUpdate;
import io.sapl.axondemo.domain.MedicalRecordAPI.CountMedicalRecordSummariesQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.CountMedicalRecordSummariesResponse;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchMedicalRecordSummariesQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchMedicalRecordSummaryQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchOxygenSaturationQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchPulseQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.FetchSinglePulseQuery;
import io.sapl.axondemo.domain.MedicalRecordAPI.MedicalRecordCreatedEvent;
import io.sapl.axondemo.domain.MedicalRecordAPI.MedicalRecordSummary;
import io.sapl.axondemo.domain.MedicalRecordAPI.MedicalRecordUpdatedEvent;
import io.sapl.axondemo.domain.MedicalRecordAPI.PulseRecord;
import io.sapl.axondemo.domain.MedicalRecordAPI.ReducedRecord;
import io.sapl.spring.method.metadata.EnforceDropWhileDenied;
import io.sapl.spring.method.metadata.EnforceRecoverableIfDenied;
import io.sapl.spring.method.metadata.EnforceTillDenied;
import io.sapl.spring.method.metadata.PostEnforce;

@Service
@Profile("backend")
@AllowReplay
@ProcessingGroup("medicalRecordProjection")
public class MedicalRecordSummaryProjection {

    private final SortedMap<String, MedicalRecordSummary> medicalSummaryReadModel;
    private final QueryUpdateEmitter queryUpdateEmitter;

    public MedicalRecordSummaryProjection(
            QueryUpdateEmitter queryUpdateEmitter
    ) {
        this.medicalSummaryReadModel = new ConcurrentSkipListMap<>();
        this.queryUpdateEmitter = queryUpdateEmitter;
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
        queryUpdateEmitter.emit(FetchMedicalRecordSummaryQuery.class, query -> query.getId().equals(summary.getId()), summary);
        queryUpdateEmitter.emit(FetchPulseQuery.class, query -> query.getId().equals(summary.getId()), new ReducedRecord(summary.getPatientName(), summary.getPulse()));
        queryUpdateEmitter.emit(FetchOxygenSaturationQuery.class, query -> query.getId().equals(summary.getId()), new ReducedRecord(summary.getPatientName(), summary.getOxygenSaturation()));
    }

    @QueryHandler
    public List<MedicalRecordSummary> handle(FetchMedicalRecordSummariesQuery query) {
        MedicalRecordSummary[] medicalRecordSummaryArray = medicalSummaryReadModel.values()
                .toArray(MedicalRecordSummary[]::new);
        return Arrays.stream(medicalRecordSummaryArray, query.getOffset(), medicalRecordSummaryArray.length)
                .limit(query.getLimit())
                .collect(Collectors.toList());
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
