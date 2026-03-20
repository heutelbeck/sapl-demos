package io.sapl.demo.spring.controller;

import io.sapl.spring.method.metadata.PreEnforce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
class ExportController {

    record ExportData(String pilotId, String sequenceId, String data) {}

    @GetMapping("/api/exportData/{pilotId}/{sequenceId}")
    @PreEnforce(action = "'exportData'",
            resource = "{'pilotId': #pilotId, 'sequenceId': #sequenceId}",
            secrets = "{'jwt': #authentication.token.tokenValue}")
    Mono<ExportData> getExportData(
            @PathVariable String pilotId,
            @PathVariable String sequenceId) {
        log.info("exportData pilotId={} sequenceId={}", pilotId, sequenceId);
        return Mono.just(new ExportData(pilotId, sequenceId, "export-payload"));
    }

}
