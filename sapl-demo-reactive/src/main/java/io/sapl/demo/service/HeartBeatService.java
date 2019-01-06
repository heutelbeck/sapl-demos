package io.sapl.demo.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class HeartBeatService {

    public Flux<Integer> getHeartBeatData() {
        return Flux.<Integer, Integer> generate(
                () -> 60,
                (state, sink) -> {
                    int diff = randomInt(10) - 4;
                    if (state + diff > 170 || state + diff < 45) {
                        state = state - diff;
                    } else {
                        state = state + diff;
                    }
                    sink.next(state);
                    simulateSomeWork(3);
                    return state;
                })
                .distinctUntilChanged()
                .subscribeOn(Schedulers.newElastic("hb-data"));
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
