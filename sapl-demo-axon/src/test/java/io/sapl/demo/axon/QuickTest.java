package io.sapl.demo.axon;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
public class QuickTest {

//	@Test
	void quick() throws InterruptedException {
		var decisions = Flux.interval(Duration.ofSeconds(1)).log("SOURCE", Level.WARNING, SignalType.ON_SUBSCRIBE,
				SignalType.CANCEL);

		var tap = tapForInitialValue(decisions);
		log.info("sub initial");

		tap.getT1().log("INITIAL", Level.INFO, SignalType.ON_NEXT, SignalType.CANCEL).subscribe();
		log.info("initial subbed");

		log.info("sub d");
		tap.getT2().take(5).log("FLUX", Level.SEVERE, SignalType.ON_NEXT, SignalType.CANCEL).blockLast();
		log.info("all taken...");
		Thread.sleep(4 * 1000);

		log.info("END");

	}

	@Test
	void quick2() throws InterruptedException {
		var origin    = Flux.interval(Duration.ofSeconds(1)).log();// .log("SOURCE", Level.INFO, SignalType.ON_NEXT,
																	// SignalType.ON_SUBSCRIBE, SignalType.CANCEL);
		var decisions = tapForInitialValue2(origin, Duration.ofSeconds(1));

		// Thread.sleep(4 * 1000);

//		.cache(1);
//
//		origin.dis
		log.info("sub initial");

		decisions.getT1().log("INITIAL", Level.INFO, SignalType.ON_NEXT, SignalType.CANCEL).block();
		log.info("initial subbed");
		Thread.sleep(3 * 1000);

		log.info("sub d");
		decisions.getT2().take(5).log("FLUX", Level.INFO, SignalType.ON_NEXT, SignalType.CANCEL).blockLast();
		log.info("all taken...");
		Thread.sleep(4 * 1000);
//		decisions.getT2().take(4).log().blockLast();
		log.info("END");

	}

	private <V> Tuple2<Mono<V>, Flux<V>> tapForInitialValue(Flux<V> source) {
		var initialSink  = Sinks.<V>one();
		var tappedSource = source.scan(Optional.<V>empty(), (x, y) -> {
								if (x.isEmpty())
									initialSink.tryEmitValue(y);
								return Optional.of(y);
							}).skip(1).map(Optional::get);
		return Tuples.of(initialSink.asMono(), tappedSource);
	}

	//@Test
	void t3() {
		Flux.range(1, 10).doOnCancel(() -> log.info("XXXXXX")).doAfterTerminate(() -> log.info("VVVVV")).log()
				.blockLast();
	}

	enum SubcriptionState {
		NONE, SUBSCRIBED, CANCELLED
	}

	record State(SubcriptionState initial, SubcriptionState updates) {

	};

	private <V> Tuple2<Mono<V>, Flux<V>> tapForInitialValue2(Flux<V> source, Duration timeout) {
		var multicastSink = Sinks.many().replay().<V>limit(1);
		var tappedSource  = source.doOnNext(v -> multicastSink.tryEmitNext(v)).subscribe();
		var state         = new State(SubcriptionState.NONE, SubcriptionState.NONE);
		var stateRef      = new AtomicReference<State>(state);
		var multicastFlux = multicastSink.asFlux();
		var initialMono   = multicastFlux.next()
				.doOnSubscribe(__ -> stateRef.getAndUpdate(s -> new State(SubcriptionState.SUBSCRIBED, s.updates)))
				.doAfterTerminate(checkInitialTermination(multicastSink, tappedSource, stateRef));
		;
		var updatesFLux = multicastFlux
				.doOnSubscribe(__ -> stateRef.getAndUpdate(s -> new State(s.initial, SubcriptionState.SUBSCRIBED)))
				.doOnCancel(checkUpdateTermination(multicastSink, tappedSource, stateRef))
				.doAfterTerminate(checkUpdateTermination(multicastSink, tappedSource, stateRef));
		Flux.interval(timeout).next().doOnNext(__ -> {
			var s = stateRef.get();
			if (s.initial == SubcriptionState.NONE || s.updates == SubcriptionState.NONE) {
				log.warn("Timeout! Decisions were not subscribed to cancel PDP subscription.");
				tappedSource.dispose();
			}
		}).subscribe();
		return Tuples.of(initialMono, updatesFLux);
	}

	private <V> Runnable checkUpdateTermination(Many<V> multicastSink, Disposable tappedSource,
			AtomicReference<State> stateRef) {
		return () -> stateRef.getAndUpdate(s -> {
			log.error("UPDATE CANCEL");
			if (s.initial == SubcriptionState.CANCELLED) {
				tappedSource.dispose();
				multicastSink.tryEmitComplete();
			}
			return new State(s.initial, SubcriptionState.CANCELLED);
		});
	}

	private <V> Runnable checkInitialTermination(Many<V> multicastSink, Disposable tappedSource,
			AtomicReference<State> stateRef) {
		return () -> stateRef.getAndUpdate(s -> {
			log.error("INIT CANCEL");
			if (s.updates == SubcriptionState.CANCELLED) {
				tappedSource.dispose();
				multicastSink.tryEmitComplete();
			}
			return new State(SubcriptionState.CANCELLED, s.updates);
		});
	}

}
