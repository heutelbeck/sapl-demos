package io.sapl.demo.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class BloodPressureService {

    public Flux<Integer> getDiastolicBloodPressureData() {
        return Flux.<Integer, Integer> generate(
                () -> 80,
                (state, sink) -> {
                    int diff = randomInt(10) - 5;
                    if (state + diff > 130 || state + diff < 65) {
                        state = state - diff;
                    } else {
                        state = state + diff;
                    }
                    sink.next(state);
                    simulateSomeWork(3);
                    return state;
                })
                .distinctUntilChanged()
                .subscribeOn(Schedulers.newElastic("dbp-data"));
    }

    public Flux<Integer> getSystolicBloodPressureData() {
        return Flux.<Integer, Integer> generate(
                () -> 120,
                (state, sink) -> {
                    int diff = randomInt(10) - 4;
                    if (state + diff > 180 || state + diff < 100) {
                        state = state - diff;
                    } else {
                        state = state + diff;
                    }
                    sink.next(state);
                    simulateSomeWork(3);
                    return state;
                })
                .distinctUntilChanged()
                .subscribeOn(Schedulers.newElastic("sbp-data"));
    }

    private void simulateSomeWork(int maxSeconds) {
        try {
            Thread.sleep(randomInt(maxSeconds * 1000));
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private Integer randomInt(int bound) {
        return new Random().nextInt(bound);
    }
}
