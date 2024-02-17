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

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.benchmark.BenchmarkExecutionContext;
import io.sapl.benchmark.util.EchoPIP;
import io.sapl.interpreter.InitializationException;
import io.sapl.pdp.PolicyDecisionPointFactory;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.util.List;

import static io.sapl.benchmark.jmh.Helper.decide;
import static io.sapl.benchmark.jmh.Helper.decideOnce;


@Slf4j
@State(Scope.Benchmark)
public class EmbeddedBenchmark {

    @Param({"{}"})
    @SuppressWarnings("unused")
    String contextJsonString;
    private PolicyDecisionPoint pdp;
    private BenchmarkExecutionContext context;

    @Setup(Level.Trial)
    public void setup() throws InitializationException {
        context = BenchmarkExecutionContext.fromString(contextJsonString);
        log.info("initializing embedded PDP");
        pdp = PolicyDecisionPointFactory.resourcesPolicyDecisionPoint(List::of, () -> List.of(EchoPIP.class),
                List::of, List::of);
    }

    @Benchmark
    public void NoAuthDecideSubscribe() {
        decide(pdp, context.authorizationSubscription);
    }

    @Benchmark
    public void NoAuthDecideOnce() {
        decideOnce(pdp, context.authorizationSubscription);
    }
}
