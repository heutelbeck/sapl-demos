/*
 * Copyright (C) 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.benchmark.jmh;

import static io.sapl.benchmark.jmh.Helper.decide;
import static io.sapl.benchmark.jmh.Helper.decideOnce;

import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.BenchmarkExecutionContext;
import io.sapl.benchmark.util.EchoPIP;
import io.sapl.interpreter.InitializationException;
import io.sapl.pdp.PolicyDecisionPointFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@State(Scope.Benchmark)
public class EmbeddedBenchmark {

    @Param({ "{}" })
    String                            contextJsonString;
    private PolicyDecisionPoint       pdp;
    private BenchmarkExecutionContext context;

    @Setup(Level.Trial)
    public void setup() throws InitializationException {
        context = BenchmarkExecutionContext.fromString(contextJsonString);
        log.info("initializing PDP and starting Benchmark ...");
        pdp = PolicyDecisionPointFactory.resourcesPolicyDecisionPoint(List::of, () -> List.of(EchoPIP.class), List::of,
                List::of);
    }

    @Benchmark
    public void noAuthDecideSubscribe() {
        decide(pdp, context.getAuthorizationSubscription());
    }

    @Benchmark
    public void noAuthDecideOnce() {
        decideOnce(pdp, context.getAuthorizationSubscription());
    }
}
