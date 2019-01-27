package io.sapl.demo.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class HeartBeatService {

    private Random rnd = new Random();

    public Flux<Integer> getHeartBeatData() {
        return Flux.<Integer, Integer> generate(
                () -> 60,
                (state, sink) -> {
                    int diff = rnd.nextInt(10) - 4;
                    if (state + diff > 170 || state + diff < 45) {
                        state = state - diff;
                    } else {
                        state = state + diff;
                    }
                    sink.next(state);
                    simulateSomeWork();
                    return state;
                })
                .distinctUntilChanged()
                .subscribeOn(Schedulers.newElastic("hb-data"));
    }

    private void simulateSomeWork() {
        try {
            Thread.sleep(Math.max(200, rnd.nextInt(3000)));
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
