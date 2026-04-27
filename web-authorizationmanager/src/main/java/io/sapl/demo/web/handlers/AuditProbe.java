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
package io.sapl.demo.web.handlers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import io.sapl.api.pdp.Decision;

/**
 * In-memory record of every audit-log obligation fired by the
 * {@link AuditLogHandler}. Lets the demo tests assert that the audit
 * obligation actually ran on every decision.
 */
@Component
public class AuditProbe {

    public record Entry(Decision decision) {}

    private final List<Entry> entries = new CopyOnWriteArrayList<>();

    public void record(Decision decision) {
        entries.add(new Entry(decision));
    }

    public List<Entry> entries() {
        return List.copyOf(entries);
    }

    public void reset() {
        entries.clear();
    }
}
