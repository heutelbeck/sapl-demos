/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.demo.webflux.testsupport;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test clock with a mutable instant. The polling thread used by SAPL's
 * {@code time.now} attribute reads {@link #instant()} concurrently with
 * the test thread mutating it; the value is held in an
 * {@link AtomicReference} so reads always see a non-null Instant.
 * <p>
 * Replaces {@code @MockitoBean Clock mockClock}: a Mockito mock returns
 * {@code null} for unstubbed calls, and {@code @MockitoBean} resets the
 * mock between tests, so the asynchronous time-PIP poller can land on
 * an unstubbed mock and produce noise. A real {@link Clock} subclass
 * with a default initial instant has neither failure mode.
 */
public final class TestClock extends Clock {

    private final AtomicReference<Instant> instant;
    private final ZoneId                   zone;

    public TestClock(Instant initial, ZoneId zone) {
        this.instant = new AtomicReference<>(initial);
        this.zone    = zone;
    }

    /** Sets the instant returned by subsequent {@link #instant()} calls. */
    public TestClock setInstant(Instant newInstant) {
        instant.set(newInstant);
        return this;
    }

    /** Advances the held instant by {@code delta}. */
    public TestClock advance(Duration delta) {
        instant.updateAndGet(current -> current.plus(delta));
        return this;
    }

    @Override
    public Instant instant() {
        return instant.get();
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId otherZone) {
        return new TestClock(instant.get(), otherZone);
    }
}
