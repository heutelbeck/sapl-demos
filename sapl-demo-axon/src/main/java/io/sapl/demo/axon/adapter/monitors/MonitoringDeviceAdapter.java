package io.sapl.demo.axon.adapter.monitors;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import io.sapl.demo.axon.command.patient.PatientCommandAPI.MonitorConnectedToPatient;
import io.sapl.demo.axon.command.patient.PatientCommandAPI.MonitorDisconnectedFromPatient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringDeviceAdapter {

	private final MonitorFactory monitorFactory;

	Map<String, Disposable> activeMonitors = new ConcurrentHashMap<>();

	@EventHandler
	void on(MonitorConnectedToPatient evt) {
		log.trace("Start {} monitor ({}))", evt.monitorType(), evt.id());
		activeMonitors.put(evt.id(), monitorFactory.createMonitor(evt.monitorDeviceId(), evt.monitorType()).subscribe());
	}

	@EventHandler
	void on(MonitorDisconnectedFromPatient evt) {
		log.trace("Stop monitor ({}))", evt.id());
		Optional.of(activeMonitors.remove(evt.id())).ifPresent(Disposable::dispose);
	}

}
