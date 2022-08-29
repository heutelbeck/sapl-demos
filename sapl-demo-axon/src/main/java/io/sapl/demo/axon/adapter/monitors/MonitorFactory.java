package io.sapl.demo.axon.adapter.monitors;

import static io.sapl.demo.axon.command.MonitorType.BLOOD_PRESSURE;
import static io.sapl.demo.axon.command.MonitorType.BODY_TEMPERATURE;
import static io.sapl.demo.axon.command.MonitorType.HEART_RATE;
import static io.sapl.demo.axon.command.MonitorType.RESPIRATION_RATE;

import java.time.Duration;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.extensions.reactor.eventhandling.gateway.ReactorEventGateway;
import org.springframework.stereotype.Service;

import io.sapl.demo.axon.command.MonitorAPI.MeasurementTaken;
import io.sapl.demo.axon.command.MonitorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorFactory {

	protected final static RandomGenerator RANDOM = RandomGenerator.getDefault();

	private final ReactorEventGateway eventGateway;

	public Flux<EventMessage<?>> createMonitor(String id, MonitorType type) {
		return switch (type) {
		case HEART_RATE -> createPulseMonitor(id);
		case RESPIRATION_RATE -> createRespirationRateMonitor(id);
		case BODY_TEMPERATURE -> createBodyTemperatureMonitor(id);
		case BLOOD_PRESSURE -> createBloodPressureMonitor(id);
		default -> {
			log.error("Unknown monitor type requested: {}", type);
			yield Flux.empty();
		}
		};
	}

	protected Flux<EventMessage<?>> createPulseMonitor(String id) {
		return randomSequence(Duration.ofSeconds(4L), 30.0D, 120.0D, 2.0D, 0.0D, 270.0D).map(
				pulse -> new MeasurementTaken(id, HEART_RATE, String.format(Locale.ENGLISH, "%3.2f", pulse), "BPM"))
				.map(GenericEventMessage::asEventMessage).flatMap(eventGateway::publish);
	}

	protected Flux<EventMessage<?>> createRespirationRateMonitor(String id) {
		return randomSequence(Duration.ofMillis(4200L), 12.0D, 25.0D, 0.5D, 0.0D, 35.0D)
				.map(pulse -> new MeasurementTaken(id, RESPIRATION_RATE, String.format(Locale.ENGLISH, "%3.2f", pulse),
						"BPM"))
				.flatMap(eventGateway::publish);
	}

	protected Flux<EventMessage<?>> createBodyTemperatureMonitor(String id) {
		return randomSequence(Duration.ofMillis(4500L), 36.5D, 37.5D, 0.1D, 25.0D, 42.3D)
				.map(pulse -> new MeasurementTaken(id, BODY_TEMPERATURE, String.format(Locale.ENGLISH, "%3.2f", pulse),
						"°C"))
				.flatMap(eventGateway::publish);
	}

	protected Flux<EventMessage<?>> createBloodPressureMonitor(String id) {
		return Flux.interval(Duration.ofSeconds(8L)).scan(randomBloodPressure(), this::nextBloodPressure)
				.map(t -> new MeasurementTaken(id, BLOOD_PRESSURE,
						String.format(Locale.ENGLISH, "%.0f/%.0f", t.getT1(), t.getT2()), "systolic/diastolic mmHg"))
				.flatMap(eventGateway::publish);
	}

	Tuple2<Double, Double> randomBloodPressure() {
		return Tuples.of(startValue(100.0D, 130.0D), startValue(60.0D, 85.0D));
	}

	Tuple2<Double, Double> nextBloodPressure(Tuple2<Double, Double> old, Long x) {
		var systolic  = nextVal(old.getT1(), 4.0D, 40.0D, 200.0D);
		var diastolic = nextVal(old.getT2(), 4.0D, 10.0D, systolic - 35.0D);
		return Tuples.of(systolic, diastolic);
	}

	Tuple2<Double, Double> nextBloodPressure(Tuple2<Double, Double> p) {
		return p;
	}

	Flux<Double> randomSequence(Duration updateIntervall, double startMin, double startMax, double deltaMax, double min,
			double max) {
		return Flux.interval(updateIntervall).scan(startValue(startMin, startMax), next(deltaMax, min, max));
	}

	double startValue(double startMin, double startMax) {
		return startMin + RANDOM.nextDouble() * (startMax - startMin);
	}

	BiFunction<Double, Long, Double> next(double deltaMax, double min, double max) {
		return (oldValue, __) -> nextVal(oldValue, deltaMax, min, max);
	}

	double nextVal(double oldValue, double deltaMax, double min, double max) {
		return limit(oldValue + (RANDOM.nextDouble() * deltaMax - deltaMax / 2.0D), min, max);
	}

	double limit(double x, double min, double max) {
		return Math.max(min, Math.min(x, max));
	}
}