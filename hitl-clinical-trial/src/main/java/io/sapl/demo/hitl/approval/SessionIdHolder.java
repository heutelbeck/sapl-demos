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
package io.sapl.demo.hitl.approval;

import lombok.experimental.UtilityClass;

/**
 * Propagates the browser session ID from the Reactor context to the tool
 * execution thread. The session ID is used exclusively for routing approval
 * requests to the originating browser tab.
 */
@UtilityClass
public class SessionIdHolder {

    public static final String CONTEXT_KEY = SessionIdHolder.class.getName();

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    public static String get() {
        return HOLDER.get();
    }

    public static void set(String sessionId) {
        HOLDER.set(sessionId);
    }

    public static void clear() {
        HOLDER.remove();
    }

}
